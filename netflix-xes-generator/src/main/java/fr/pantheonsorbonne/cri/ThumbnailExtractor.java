package fr.pantheonsorbonne.cri;

import java.io.IOException;
import java.util.function.BiConsumer;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Thumbnail;
import fr.pantheonsorbonne.ufr27.miage.model.stream4good.ThumbnailData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;

class ThumbnailExtractor extends CachedEventesourceExtractor<ThumbnailData> {

	public ThumbnailExtractor(Session session, Client client, TraceType trace, CachedResource cache) {
		super(session, client, trace, cache);

	}

	@Override
	public BiConsumer<TraceType, ThumbnailData> getConsumer() {
		return new ThumbnailDataConsumer(client, trace);
	}

	@Override
	public void extractResource() {
		extractResource("thumbnails", ThumbnailData.class);

	}

	class ThumbnailDataConsumer implements BiConsumer<TraceType, ThumbnailData> {
		private final Client client;
		private final TraceType trace;

		ThumbnailDataConsumer(Client client, TraceType trace) {
			this.client = client;
			this.trace = trace;
		}

		@Override
		public void accept(TraceType t, ThumbnailData u) {
			for (Thumbnail w : u.getThumbnails()) {
				String country;
				try {
					country = App.getCountryForVideoId(client, w.getVideoId());
					trace.getEvents().add(eventFactory.getThumbnailEvent(w.getVideoId(), w.getRow(), w.getCol(),
							w.getTimestamp(), country));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}
	}
}