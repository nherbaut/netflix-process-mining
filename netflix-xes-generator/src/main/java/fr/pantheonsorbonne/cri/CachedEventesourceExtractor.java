package fr.pantheonsorbonne.cri;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

public abstract class CachedEventesourceExtractor<T> extends EventResourceExtractor<T> {

	private CachedResource cache;

	CachedEventesourceExtractor(Session session, Client client, TraceType trace, CachedResource cache) {
		super(session, client, trace);
		this.cache = cache;
	}

	@Override
	public CachedResource getCache() {
		return this.cache;
	}

}
