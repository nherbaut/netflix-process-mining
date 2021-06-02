package fr.pantheonsorbonne.cri.primespace;

import java.util.function.BiConsumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.pantheonsorbonne.cri.cache.CachedResource;
import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.cri.model.stream4good.ThumbnailData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

public class SessionExtractor extends CountryAwareCachedEventSourceExtractor<Session> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SessionExtractor.class);

	public SessionExtractor(Session session, Client client, TraceType trace, CachedResource cache) {
		super(session, client, trace, cache);
		LOGGER.trace("SessionExtractor for session {} for trace {}", session.getSession_id(), trace.hashCode());
	}

	@Override
	public BiConsumer<TraceType, Session> getConsumer() {
		return new BiConsumer<TraceType, Session>() {

			@Override
			public void accept(TraceType t, Session u) {

				trace.getEvents().add(eventFactory.getStartSessionEvent(u.getCreation_date(),
						session.getUserData().getUser().getUser_id()));
			}
		};
	}

	@Override
	protected void extractResource() {
		this.getConsumer().accept(trace, session);

	}

}
