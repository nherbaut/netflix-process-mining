package fr.pantheonsorbonne.cri.xes;

import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.cri.primespace.EventResourceExtractor;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

public abstract class RunnableEventResourceExtractor<T> extends EventResourceExtractor<T> implements Runnable {

	public RunnableEventResourceExtractor(Session session, Client client, TraceType trace) {
		super(session, client, trace);

	}

	@Override
	public void run() {
		extractResource();

	}

	protected abstract void extractResource();

}
