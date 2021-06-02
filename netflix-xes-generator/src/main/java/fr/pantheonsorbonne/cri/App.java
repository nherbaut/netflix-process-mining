package fr.pantheonsorbonne.cri;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import ch.qos.logback.classic.Level;
import fr.pantheonsorbonne.cri.cache.CachedResource;
import fr.pantheonsorbonne.cri.cache.ConcurrentMapCachedResource;
import fr.pantheonsorbonne.cri.cache.RedisCachedResource;
import fr.pantheonsorbonne.cri.helper.Helper;
import fr.pantheonsorbonne.cri.log.Inspector;
import fr.pantheonsorbonne.cri.model.oauth2.Oauth2Response;
import fr.pantheonsorbonne.cri.model.stream4good.UserData;
import fr.pantheonsorbonne.cri.primespace.ParallelTraceFactory;
import fr.pantheonsorbonne.cri.xes.EventFactory;
import fr.pantheonsorbonne.cri.xes.XesFactory;
import fr.pantheonsorbonne.cri.xes.XesFactory.NETFLIX_ASSET_TYPE;
import fr.pantheonsorbonne.cri.xes.XesFactory.XES_ATTR;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeDateType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeIntType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeStringType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.EventType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import io.lettuce.core.RedisConnectionException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "netflix-xes-generator")
public class App implements Runnable {

	private ExecutorService EXECUTOR;

	public static CachedResource CACHE;
	private static final ObjectFactory OBJECT_XES_FACTORY = new ObjectFactory();
	public static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final XesFactory XES_FACTORY = new XesFactory(OBJECT_XES_FACTORY);

	@Option(names = { "--thread-count" }, description = "number of thread to use in the xes generation")
	private Integer threadCount = 50;

	@Option(names = { "-o", "--output" }, description = "path of the output file")
	private String targetFileName = "netflix.xes";

	@Option(names = { "-r", "--reddis-server-address" }, description = "the address of the redis cache server to use")
	private String redisConnectionString = "redis://localhost:6379/0";

	@Option(names = { "-a", "--base-api-address" }, description = "This is the root of the API to use")
	private String baseAPIAddress = "https://api.vod-prime.space/api";

	@Option(names = { "-t", "--base-auth-address" }, description = "This is the base authentication api to use")
	private String baseAuthApiAddress = "https://auth.vod-prime.space/auth/realms/discoverability/protocol/openid-connect/token";

	@Option(names = { "--auth-scope" }, description = "Scope of the Auth api")
	private String authAPIScope = "dashboard-vuejs";

	@Option(names = { "--auth-client-id" }, description = "Client id of the Auth api")
	private String authAPIClientId = "dashboard-vuejs";

	@Option(names = { "-u", "--user" }, description = "User to pass to the auth api")
	private String authUser = "user1";

	@Option(names = { "-p", "--password" }, description = "Password to pass to the auth api")
	private String authPassword = "user";

	@Option(names = { "--netflix-content-api" }, description = "The adress of the content api for netflix")
	public String platformApiURI = "https://platform-api.vod-prime.space/api/emns/provider/4/identifier/";

	@SuppressWarnings("unchecked")
	@Option(names = {
			"--user-whitelist" }, split = ",", description = "an optional list of users to generate the xes for")
	public List<String> userWhiteList = Collections.EMPTY_LIST;

	public String getPlatformApiURI() {
		return platformApiURI;
	}

	@Option(names = { "-h", "--help" }, usageHelp = true, description = "display a help message")
	private boolean helpRequested = false;

	@Option(names = { "-l", "--log-level" }, description = "change the log level (TRACE,DEBUG,INFO,WARN,ERROR)")
	private String logLevel = "INFO";

	private static App app;

	public static App getInstance() {
		if (app == null) {

			app = new App();

		}
		return app;
	}

	private App() {

	}

	public static void main(String[] args) throws JAXBException, IOException, DatatypeConfigurationException {
		int exitCode = new CommandLine(App.getInstance()).execute(args);
		System.exit(exitCode);
	}

	private static class EventXesTimeStampComparator implements Comparator<EventType> {
		@Override
		public int compare(EventType o1, EventType o2) {
			AttributeType timestampO1 = o1.getStringsAndDatesAndInts().stream()
					.filter(a -> a.getKey().equals(XES_ATTR.TIMESTAMP.getXesName())).findAny().orElseThrow();
			AttributeType timestampO2 = o2.getStringsAndDatesAndInts().stream()
					.filter(a -> a.getKey().equals(XES_ATTR.TIMESTAMP.getXesName())).findAny().orElseThrow();

			return ((AttributeDateType) timestampO1).getValue().compare(((AttributeDateType) timestampO2).getValue());
		}
	}

	private static NETFLIX_ASSET_TYPE getNetflixTypeForEvent(EventType o) {
		return NETFLIX_ASSET_TYPE.valueOf(o.getStringsAndDatesAndInts().stream()
				.filter(a -> a.getKey().equals(XES_ATTR.NETFLIX_ASSET_TYPE.getXesName()))
				.map(a -> (AttributeStringType) a).findAny().orElseThrow(new Supplier<RuntimeException>() {

					@Override
					public RuntimeException get() {
						return new RuntimeException(o.toString() + " does not have and asset type");
					}
				}).getValue());
	}

	private static long getRankForLolomo(EventType o) {
		return getIntAttr(o, XES_ATTR.RANK);
	}

	private static long getRowForThumbnail(EventType o) {
		return getIntAttr(o, XES_ATTR.ROW);
	}

	private static long getColForThumbnail(EventType o) {
		return getIntAttr(o, XES_ATTR.COL);
	}

	private static long getIntAttr(EventType o, XES_ATTR attrType) {
		return ((AttributeIntType) (o.getStringsAndDatesAndInts().stream()
				.filter(a -> a.getKey().equals(attrType.getXesName())).findAny()
				.orElseThrow(new Supplier<RuntimeException>() {

					@Override
					public RuntimeException get() {
						return new RuntimeException(o.toString() + " does not have attr type" + attrType.toString());
					}
				}))).getValue();
	}

	private static Instant getDateAttr(EventType o) {
		return Helper.getInstant(((AttributeDateType) (o.getStringsAndDatesAndInts().stream()
				.filter(a -> a.getKey().equals(XES_ATTR.TIMESTAMP.getXesName())).findAny().orElseThrow(new Supplier<RuntimeException>() {

					@Override
					public RuntimeException get() {
						return new RuntimeException(o.toString()+" does not have a timestamp");
					}
				}))).getValue());
	}

	private static void setTimeStamp(EventType o,Instant i) {
		AttributeDateType timestamp = (AttributeDateType) (o.getStringsAndDatesAndInts().stream()
				.filter(a -> a.getKey().equals(XES_ATTR.TIMESTAMP.getXesName())).findAny().orElseThrow());
		timestamp .setValue(Helper.toDate(i));
	}
	
	private static String getSessionIdFromTrace(TraceType t) {
		return ((AttributeStringType) (t.getStringsAndDatesAndInts().stream().filter(a -> a.getKey().equals(XES_ATTR.ORG_RESOURCE.getXesName())).findAny().orElseThrow())).getValue();
	}

	private static class EventXesTimeStampComparatorStrategy1 implements Comparator<EventType> {
		@Override
		public int compare(EventType o1, EventType o2) {
			NETFLIX_ASSET_TYPE t1 = getNetflixTypeForEvent(o1);
			NETFLIX_ASSET_TYPE t2 = getNetflixTypeForEvent(o2);
			if (t1.equals(NETFLIX_ASSET_TYPE.LOLOMO) && t2.equals(NETFLIX_ASSET_TYPE.LOLOMO)) {
				return Long.compare(getRankForLolomo(o1), getRankForLolomo(o2));
			}
			if (t1.equals(NETFLIX_ASSET_TYPE.THUMBNAIL) && t2.equals(NETFLIX_ASSET_TYPE.LOLOMO)) {
				return Long.compare(getRowForThumbnail(o1), getRankForLolomo(o2));
			}
			if (t1.equals(NETFLIX_ASSET_TYPE.LOLOMO) && t2.equals(NETFLIX_ASSET_TYPE.THUMBNAIL)) {
				return Long.compare(getRankForLolomo(o1), getRowForThumbnail(o2));
			}

			if (t1.equals(NETFLIX_ASSET_TYPE.THUMBNAIL) && t2.equals(NETFLIX_ASSET_TYPE.THUMBNAIL)) {
				int rowComparison = Long.compare(getRowForThumbnail(o1), getRowForThumbnail(o2));
				if (rowComparison != 0) {
					return rowComparison;
				} else {
					return Long.compare(getRowForThumbnail(o1), getRowForThumbnail(o2));
				}

			}

			if (t1.equals(NETFLIX_ASSET_TYPE.SESSION_START)) {
				return -1;
			}
			if (t2.equals(NETFLIX_ASSET_TYPE.SESSION_START)) {
				return 1;
			}
			if (t1.equals(NETFLIX_ASSET_TYPE.WATCH) && !t2.equals(NETFLIX_ASSET_TYPE.WATCH)) {
				return 1;
			}
			if (!t1.equals(NETFLIX_ASSET_TYPE.WATCH) && t2.equals(NETFLIX_ASSET_TYPE.WATCH)) {
				return -1;
			}
			if (t1.equals(NETFLIX_ASSET_TYPE.WATCH) && t2.equals(NETFLIX_ASSET_TYPE.WATCH)) {
				return getDateAttr(o1).compareTo(getDateAttr(o2));
			}
			throw new RuntimeException("failed to compare two events of types " + t1 + " " + t2);
		}
	}

	// well it sucks that EventType and TraceType arn't related in XES xsd...
	private static class TracesXesTimeStampComparator implements Comparator<TraceType> {
		@Override
		public int compare(TraceType o1, TraceType o2) {
			AttributeType timestampO1 = o1.getStringsAndDatesAndInts().stream()
					.filter(a -> a.getKey().equals(XES_ATTR.TIMESTAMP.getXesName())).findAny().orElseThrow();
			AttributeType timestampO2 = o2.getStringsAndDatesAndInts().stream()
					.filter(a -> a.getKey().equals(XES_ATTR.TIMESTAMP.getXesName())).findAny().orElseThrow();

			return ((AttributeDateType) timestampO1).getValue().compare(((AttributeDateType) timestampO2).getValue());
		}
	}

	static class CachedResourceFactory {
		public static CachedResource getCachedResource(String redisConnectionString) {
			try {
				return new RedisCachedResource(redisConnectionString);
			} catch (RedisConnectionException e) {
				LOGGER.warn("Using default in-memory cache strategy, please configure redis for better performances");
				return new ConcurrentMapCachedResource();
			}
		}
	}

	@Override
	public void run() {
		try {

			final Level level = Level.toLevel(logLevel, null);
			((ch.qos.logback.classic.Logger) LoggerFactory.getLogger(App.class.getPackageName())).setLevel(level);

			EXECUTOR = Executors.newFixedThreadPool(threadCount);
			CACHE = CachedResourceFactory.getCachedResource(redisConnectionString);
			Log log = XES_FACTORY.getLog();

			Client client = ClientBuilder.newClient();
			WebTarget target = client.target(baseAuthApiAddress);
			MultivaluedMap<String, String> formData = new MultivaluedStringMap(5);
			formData.add("client_id", authAPIClientId);
			formData.add("grant_type", "password");
			formData.add("scope", authAPIScope);
			formData.add("username", authUser);
			formData.add("password", authPassword);

			Response resp = target.request().accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));

			Oauth2Response oauth2Resp = resp.readEntity(Oauth2Response.class);

			Feature feature = OAuth2ClientSupport.feature(oauth2Resp.access_token);
			client.register(feature);

			WebTarget apiTarget = client.target(baseAPIAddress);

			Response resp2 = apiTarget.path("users").queryParam("limit", 2).request().accept(MediaType.APPLICATION_JSON)
					.get();

			JsonReader reader = new JsonReader(new InputStreamReader((InputStream) resp2.getEntity()));

			Inspector gadget = new Inspector(log, EXECUTOR);
			new Thread(gadget).start();

			reader.beginArray();

			Collection<ParallelTraceFactory> factories = new ArrayList<ParallelTraceFactory>();

			while (reader.hasNext()) {
				UserData userData = new Gson().fromJson(reader, UserData.class);
				if (userWhiteList.isEmpty() || userWhiteList.contains(userData.getUser().getUser_id())) {
					ParallelTraceFactory ptf = new ParallelTraceFactory(OBJECT_XES_FACTORY, client, userData, log,
							EXECUTOR, CACHE);
					factories.add(ptf);
					EXECUTOR.submit(() -> {
						ptf.extractEvents();
					});
				}

			}

			try {
				Thread.currentThread().sleep(10000L);
				EXECUTOR.shutdown();
				EXECUTOR.awaitTermination(5L, TimeUnit.MINUTES);
				factories.stream().forEach(f -> f.repportTasks());

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			log.getTraces().sort(new TracesXesTimeStampComparator());
			for (TraceType trace : log.getTraces()) {
				trace.getEvents().sort(new EventXesTimeStampComparatorStrategy1());
				Instant startSessionTimestamp = getDateAttr(trace.getEvents().get(0));
				//if no lolomo-0 is reccorded, add it
				if(trace.getEvents().size()>1 && !getNetflixTypeForEvent(trace.getEvents().get(1)).equals(NETFLIX_ASSET_TYPE.LOLOMO)) {
					trace.getEvents().add(1, getFakeBillboardLolomo(trace, startSessionTimestamp)); 
				}
				
				for (EventType event : trace.getEvents()) {
					setTimeStamp(event, startSessionTimestamp);
					startSessionTimestamp=startSessionTimestamp.plusMillis(10);
				}
			}

			JAXBContext context = JAXBContext.newInstance(Log.class);
			Writer writer = new FileWriter(targetFileName);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(log, writer);
			writer.close();
			CACHE.close();
			gadget.setStopped(true);
			LOGGER.info("results written in {}", targetFileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private EventType getFakeBillboardLolomo(TraceType trace, Instant startSessionTimestamp) {
		return new EventFactory(new ObjectFactory()).getLolomoEvent("billboard", "",
				0, startSessionTimestamp , getSessionIdFromTrace(trace));
	}

}
