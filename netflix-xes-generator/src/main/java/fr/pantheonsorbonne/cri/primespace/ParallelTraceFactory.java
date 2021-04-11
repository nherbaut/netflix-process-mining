package fr.pantheonsorbonne.cri.primespace;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

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
	private Log log;

	public ParallelTraceFactory(ObjectFactory factoryXes, Client client, UserData userData, Log log,
			ExecutorService executor, CachedResource cache) {

		super(factoryXes, userData);
		this.client = client;
		this.executor = executor;
		this.cache = cache;
		this.log = log;

	}

	private final ExecutorService executor;
	private final CachedResource cache;
	private final Queue<Future<?>> tasks = new LinkedList<>();

	public void extractEvents() {

		if (true || userData.getUser().getUser_id().equals("a01efb7d-c6fa-4f1d-b79d-41e74234af79")
				&& userData.getSessions().size() > 10) {

			for (Session session : userData.getSessions()) {
				session.setUserData(userData);

				TraceType trace = new UserExtractor(factoryXes, session, log).getUserTrace();
				log.getTraces().add(trace);

				session.setUserData(userData);

				tasks.add(executor.submit(() -> {
					new SessionExtractor(session, client, trace, cache).extractResource();
				}));
				tasks.add(executor.submit(() -> {
					new LolomoExtractor(session, client, trace, cache).extractResource();
				}));

				tasks.add(executor.submit(() -> {
					new ThumbnailExtractor(session, client, trace, cache).extractResource();
				}));

				tasks.add(executor.submit(() -> {
					new WatchesExtractor(session, client, trace, cache).extractResource();
				}));

			}
		}

	}

	@Override
	public CachedResource getCache() {
		return this.cache;
	}

	public void repportTasks() {
		for (Future<?> task : tasks) {
			try {

				task.get();
			} catch (ExecutionException | InterruptedException ee) {
				ee.printStackTrace();
			}
		}
	}

}
