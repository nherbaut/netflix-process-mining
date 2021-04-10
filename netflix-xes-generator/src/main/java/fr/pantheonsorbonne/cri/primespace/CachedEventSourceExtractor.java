package fr.pantheonsorbonne.cri.primespace;

import fr.pantheonsorbonne.cri.cache.CachedResource;
import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

public abstract class CachedEventSourceExtractor<T> extends EventResourceExtractor<T> {

	private CachedResource cache;

	public CachedEventSourceExtractor(Session session, Client client, TraceType trace, CachedResource cache) {
		super(session, client, trace);
		this.cache = cache;
	}

	@Override
	public CachedResource getCache() {
		return this.cache;
	}

}
