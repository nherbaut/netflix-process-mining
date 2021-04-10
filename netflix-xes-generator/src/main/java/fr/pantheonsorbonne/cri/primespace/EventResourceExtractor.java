package fr.pantheonsorbonne.cri.primespace;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Optional;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import fr.pantheonsorbonne.cri.model.stream4good.LinkedResource;
import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.cri.xes.EventFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public abstract class EventResourceExtractor<T> implements CachedResourceExtractor, ResourceExtractor<TraceType, T> {

	protected final EventFactory eventFactory = new EventFactory(new ObjectFactory());

	final protected TraceType trace;
	final protected Session session;
	final protected Client client;

	protected EventResourceExtractor(Session session, Client client, TraceType trace) {
		this.trace = trace;
		this.session = session;
		this.client = client;

	}

	public void extractResource(String linkName, Class<T> klass) {
		try {
			extractResource(client, trace, session, linkName, klass);
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	private void extractResource(Client client, TraceType trace, LinkedResource res, String linkName, Class<T> klass)
			throws IOException {
		{

			WebTarget target = res.getLinkTarget(linkName, client);
			String key = target.getUri().toASCIIString();
			Optional<T> optionalLolomoData = this.getCache().lookup(key, klass);
			T data = null;
			if (optionalLolomoData.isEmpty()) {
				InputStream is = (InputStream) target.request().accept(MediaType.APPLICATION_JSON).get().getEntity();
				try (JsonReader reader = new JsonReader(new InputStreamReader(is))) {
					data = new Gson().fromJson(reader, klass);
					this.getCache().cache(key, data);
				}

			} else {
				data = optionalLolomoData.get();
			}

			getConsumer().accept(trace, data);

		}
	}

	protected abstract void extractResource();

}
