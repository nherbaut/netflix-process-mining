package fr.pantheonsorbonne.cri;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.glassfish.jersey.client.oauth2.OAuth2ClientSupport;
import org.glassfish.jersey.internal.util.collection.MultivaluedStringMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import fr.pantheonsorbonne.cri.cache.CachedResource;
import fr.pantheonsorbonne.cri.cache.RedisCachedResource;
import fr.pantheonsorbonne.cri.log.Inspector;
import fr.pantheonsorbonne.cri.model.oauth2.Oauth2Response;
import fr.pantheonsorbonne.cri.model.stream4good.Country;
import fr.pantheonsorbonne.cri.model.stream4good.CountryData;
import fr.pantheonsorbonne.cri.model.stream4good.UserData;
import fr.pantheonsorbonne.cri.primespace.ParallelTraceFactory;
import fr.pantheonsorbonne.cri.xes.XesFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ExtensionType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.GlobalsType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.core.Response;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "netflix-xes-generator")
public class App {

	private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(1);
	public static CachedResource CACHE;
	private static final ObjectFactory OBJECT_XES_FACTORY = new ObjectFactory();
	public static final Logger LOGGER = LoggerFactory.getLogger(App.class);
	private static final XesFactory XES_FACTORY = new XesFactory(OBJECT_XES_FACTORY);

	@Option(names = { "-o", "--output" }, description = "path of the output file")
	private static String targetFileName = "netflix.xes";

	@Option(names = { "-r", "--reddis-server-address" }, description = "the address of the redis cache server to use")
	private static String redisConnectionString = "redis://localhost:6379/0";

	@Option(names = { "-a", "--base-api-address" }, description = "This is the root of the API to use")
	private static String baseAPIAddress = "https://api.vod-prime.space/api";

	@Option(names = { "-t", "--base-auth-address" }, description = "This is the base authentication api to use")
	private static String baseAuthApiAddress = "https://auth.vod-prime.space/auth/realms/discoverability/protocol/openid-connect/token";

	@Option(names = { "--auth-scope" }, description = "Scope of the Auth api")
	private static String authAPIScope = "dashboard-vuejs";

	@Option(names = { "--auth-client-id" }, description = "Client id of the Auth api")
	private static String authAPIClientId = "dashboard-vuejs";

	@Option(names = { "-u", "--user" }, description = "User to pass to the auth api")
	private static String authUser = "user1";

	@Option(names = { "-p", "--password" }, description = "Password to pass to the auth api")
	private static String authPassword = "user";

	@Option(names = { "--netflix-content-api" }, description = "The adress of the content api for netflix")
	public static String platformApiURI = "https://platform-api.vod-prime.space/api/emns/provider/4/identifier/";

	public static void main(String[] args) throws JAXBException, IOException, DatatypeConfigurationException {

		CACHE = new RedisCachedResource(redisConnectionString);
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

		Inspector gadget = new Inspector(log);
		new Thread(gadget).start();

		reader.beginArray();

		Collection<ParallelTraceFactory> factories = new ArrayList<ParallelTraceFactory>();

		while (reader.hasNext()) {
			UserData userData = new Gson().fromJson(reader, UserData.class);
			ParallelTraceFactory ptf = new ParallelTraceFactory(OBJECT_XES_FACTORY, client, userData, log, EXECUTOR,
					CACHE);
			factories.add(ptf);
			EXECUTOR.submit(() -> {
				ptf.extractEvents();
			});

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

		JAXBContext context = JAXBContext.newInstance(Log.class);
		Writer writer = new FileWriter(targetFileName);
		context.createMarshaller().marshal(log, writer);
		writer.close();
		CACHE.close();
		gadget.setStopped(true);

	}

}
