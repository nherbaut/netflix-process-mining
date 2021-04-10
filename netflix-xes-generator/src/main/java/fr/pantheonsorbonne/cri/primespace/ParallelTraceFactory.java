package fr.pantheonsorbonne.cri.primespace;

import java.util.concurrent.ExecutorService;

import fr.pantheonsorbonne.cri.cache.CachedResource;
import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.cri.model.stream4good.UserData;
import fr.pantheonsorbonne.cri.xes.TraceFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

public class ParallelTraceFactory extends TraceFactory implements CachedResourceExtractor {

	private Client client;
	private TraceType trace;

	public ParallelTraceFactory(ObjectFactory factoryXes, Client client, UserData userData, Log log,
			ExecutorService executor, CachedResource cache) {

		super(factoryXes, userData);
		this.client = client;
		this.executor = executor;
		this.cache = cache;
		this.trace = new UserExtractor(factoryXes, userData, log).getUserTrace();

		log.getTraces().add(this.trace);

	}

	private final ExecutorService executor;
	private final CachedResource cache;

	public void extractEvents() {

		for (Session session : userData.getSessions()) {
			executor.submit(() -> {
				new ThumbnailExtractor(session, client, trace, cache).extractResource();
			});

			executor.submit(() -> {
				new WatchesExtractor(session, client, trace, cache).extractResource();
			});

			executor.submit(() -> {
				new LolomoExtractor(session, client, trace, cache).extractResource();
			});
		}

	}

	@Override
	public CachedResource getCache() {
		return this.cache;
	}

}
