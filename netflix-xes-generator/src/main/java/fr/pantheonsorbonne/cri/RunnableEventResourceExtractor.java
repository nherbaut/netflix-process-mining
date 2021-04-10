package fr.pantheonsorbonne.cri;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

public abstract class RunnableEventResourceExtractor<T> extends EventResourceExtractor<T> implements Runnable {

	RunnableEventResourceExtractor(Session session, Client client, TraceType trace) {
		super(session, client, trace);

	}

	@Override
	public void run() {
		extractResource();

	}

	protected abstract void extractResource();

}
