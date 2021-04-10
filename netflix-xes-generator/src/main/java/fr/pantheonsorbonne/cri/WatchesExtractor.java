package fr.pantheonsorbonne.cri;

import java.io.IOException;
import java.util.function.BiConsumer;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Watch;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.WatchData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

class WatchesExtractor extends CachedEventesourceExtractor<WatchData> {

	WatchesExtractor(Session session, Client client, TraceType trace, CachedResource cache) {
		super(session, client, trace, cache);

	}

	class WatchDataConsumer implements BiConsumer<TraceType, WatchData> {
		private final Client client;
		private final TraceType trace;

		WatchDataConsumer(Client client, TraceType trace) {
			this.client = client;
			this.trace = trace;
		}

		@Override
		public void accept(TraceType t, WatchData u) {
			for (Watch w : u.getWatches()) {
				String country;
				try {
					country = App.getCountryForVideoId(client, w.getVideo_id());
					trace.getEvents().add(eventFactory.getWatchEvent(w.getVideo_id(), w.getDuration_seconds(),
							w.getTimestamp(), country));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}

	@Override
	public BiConsumer<TraceType, WatchData> getConsumer() {
		return new WatchDataConsumer(client, trace);
	}

	@Override
	public void extractResource() {
		this.extractResource("watches", WatchData.class);

	}

}