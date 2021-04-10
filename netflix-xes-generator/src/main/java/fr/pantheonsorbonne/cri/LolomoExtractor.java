package fr.pantheonsorbonne.cri;

import java.util.function.BiConsumer;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Lolomo;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.LolomoData;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

class LolomoExtractor extends CachedEventesourceExtractor<LolomoData> {

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
				trace.getEvents().add(eventFactory.getLolomoEvent(lolomo));
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