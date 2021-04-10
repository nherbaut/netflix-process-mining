package fr.pantheonsorbonne.cri;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Content;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.IMDBData;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.IMDBEntry;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.UserData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;

/**
 * Hello world!
 *
 */
public class App {

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(50);
	static final CachedResource CACHE = new RedisCachedResource();
	private static final ObjectFactory XES_FACTORY = new ObjectFactory();
	private static final XesFactory eventFactory = new EventFactory(XES_FACTORY);
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	private static IMDBEntry getIMDBData(String videoId, Client client) throws IOException {
		Optional<IMDBEntry> optionalIMDBEntry = CACHE.lookup(videoId, IMDBEntry.class);
		if (optionalIMDBEntry == null) {
			return null;
		} else if (optionalIMDBEntry.isEmpty()) {
			try (JsonReader reader = new JsonReader(new InputStreamReader((InputStream) client
					.target(new URI("https://platform-api.vod-prime.space/api/emns/provider/4/identifier/" + videoId))
					.request().accept(MediaType.APPLICATION_JSON).get().getEntity()))) {
				WebTarget target = ((Content) new Gson().fromJson(reader, Content.class)).getLinkTarget("imdb_id",
						client);
				try (JsonReader reader2 = new JsonReader(new InputStreamReader(
						(InputStream) target.request().accept(MediaType.APPLICATION_JSON).get().getEntity()))) {
					IMDBEntry entry = ((IMDBData) new Gson().fromJson(reader2, IMDBData.class)).getData();
					CACHE.cache(videoId, entry);
					return entry;
				}
			} catch (Throwable e) {
				LOGGER.trace(videoId + " not found");
				CACHE.cache(videoId, IMDBEntry.class);
				return null;
			}

		} else {
			return optionalIMDBEntry.get();
		}
	}

	static XMLGregorianCalendar toDate(Instant instant) {
		String dateTimeString = instant.toString();
		XMLGregorianCalendar date2;
		try {
			date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return date2;
	}

	public static void main(String[] args) throws JAXBException, IOException, DatatypeConfigurationException {
		AtomicInteger counter = new AtomicInteger(0);

		Log log = XES_FACTORY.createLog();

		Client client = ClientBuilder.newClient();
		WebTarget target = client.target("https://auth.vod-prime.space");
		MultivaluedMap<String, String> formData = new MultivaluedStringMap(5);
		formData.add("client_id", "dashboard-vuejs");
		formData.add("grant_type", "password");
		formData.add("scope", "dashboard-vuejs");
		formData.add("username", "user1");
		formData.add("password", "user");

		Response resp = target.path("auth/realms/discoverability/protocol/openid-connect/token").request()
				.accept(MediaType.APPLICATION_JSON).post(Entity.form(formData));

		Oauth2Response oauth2Resp = resp.readEntity(Oauth2Response.class);

		Feature feature = OAuth2ClientSupport.feature(oauth2Resp.access_token);
		client.register(feature);

		WebTarget apiTarget = client.target("https://api.vod-prime.space/api");

		Response resp2 = apiTarget.path("users").queryParam("limit", 2).request().accept(MediaType.APPLICATION_JSON)
				.get();

		JsonReader reader = new JsonReader(new InputStreamReader((InputStream) resp2.getEntity()));

		Inspector gadget = new Inspector(log);
		new Thread(gadget).start();

		reader.beginArray();
		while (reader.hasNext()) {
			UserData userData = new Gson().fromJson(reader, UserData.class);

			EXECUTOR.submit(() -> {
				new ParallelTraceFactory(XES_FACTORY, client, userData, log, EXECUTOR, CACHE).extractEvents();
			});

		}

		try {
			Thread.currentThread().sleep(10000L);
			EXECUTOR.shutdown();
			EXECUTOR.awaitTermination(5L, TimeUnit.MINUTES);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JAXBContext context = JAXBContext.newInstance(Log.class);
		Writer writer = new FileWriter("/home/nherbaut/toto.xes");
		context.createMarshaller().marshal(log, writer);
		writer.close();
		gadget.setStopped(true);

	}

	static String getCountryForVideoId(Client client, String videoId) throws IOException {
		String country = "unknown";
		IMDBEntry imdbData = getIMDBData(videoId, client);
		if (imdbData != null && imdbData.getCountries() != null) {
			try {
				country = imdbData.getCountries().stream().findFirst().orElseThrow().getCountry();
			} catch (NoSuchElementException nsee) {
				//
			}
		}
		return country;
	}

}
