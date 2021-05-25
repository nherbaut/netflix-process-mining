package fr.pantheonsorbonne.cri.primespace;

import java.time.Instant;
import java.util.function.BiConsumer;

import fr.pantheonsorbonne.cri.cache.CachedResource;
import fr.pantheonsorbonne.cri.model.stream4good.Lolomo;
import fr.pantheonsorbonne.cri.model.stream4good.LolomoData;
import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

class LolomoExtractor extends CachedEventSourceExtractor<LolomoData> {

	LolomoExtractor(Session session, Client client, TraceType trace, CachedResource cache) {
		super(session, client, trace, cache);

	}

	class LolomoDataConsumer implements BiConsumer<TraceType, LolomoData> {
		private final TraceType trace;

		LolomoDataConsumer(TraceType trace) {
			this.trace = trace;
		}

		@Override
		public void accept(TraceType t, LolomoData u) {
			for (Lolomo lolomo : u.getLolomos()) {
				Instant timestamp = session.getCreation_date().plusSeconds(1);
				//Instant timestamp = lolomo.getTimestamp();
				trace.getEvents().add(eventFactory.getLolomoEvent(lolomo.getType(), lolomo.getAssociated_content(),
						lolomo.getRank(), timestamp, session.getUserData().getUser().getUser_id()));
			}

		}
	}

	@Override
	public BiConsumer<TraceType, LolomoData> getConsumer() {
		return new LolomoDataConsumer(this.trace);
	}

	@Override
	public void extractResource() {
		extractResource("lolomos", LolomoData.class);

	}

}