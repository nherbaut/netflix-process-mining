package fr.pantheonsorbonne.cri.primespace;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import fr.pantheonsorbonne.cri.cache.CachedResource;
import fr.pantheonsorbonne.cri.model.stream4good.Content;
import fr.pantheonsorbonne.cri.model.stream4good.IMDBData;
import fr.pantheonsorbonne.cri.model.stream4good.IMDBEntry;
import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;

public abstract class CountryAwareCachedEventSourceExtractor<T> extends CachedEventSourceExtractor<T> {

	public static final Logger LOGGER = LoggerFactory.getLogger(CountryAwareCachedEventSourceExtractor.class);

	public CountryAwareCachedEventSourceExtractor(Session session, Client client, TraceType trace, CachedResource cache) {
		super(session, client, trace, cache);

	}

	protected String getCountryForVideoId(Client client, String videoId) throws IOException {
		String country = "unknown";
		IMDBEntry imdbData = getIMDBData(videoId, client);
		if (imdbData != null && imdbData.getCountries() != null) {
			try {
				country = imdbData.getCountries().stream().findFirst().orElseThrow().getCountry();
			} catch (NoSuchElementException nsee) {
				//
			}
		}
		return country;
	}

	public IMDBEntry getIMDBData(String videoId, Client client) throws IOException {
		Optional<IMDBEntry> optionalIMDBEntry = this.getCache().lookup(videoId, IMDBEntry.class);
		if (optionalIMDBEntry == null) {
			return null;
		} else if (optionalIMDBEntry.isEmpty()) {
			try (JsonReader reader = new JsonReader(new InputStreamReader((InputStream) client
					.target(new URI("https://platform-api.vod-prime.space/api/emns/provider/4/identifier/" + videoId))
					.request().accept(MediaType.APPLICATION_JSON).get().getEntity()))) {
				WebTarget target = ((Content) new Gson().fromJson(reader, Content.class)).getLinkTarget("imdb_id",
						client);
				try (JsonReader reader2 = new JsonReader(new InputStreamReader(
						(InputStream) target.request().accept(MediaType.APPLICATION_JSON).get().getEntity()))) {
					IMDBEntry entry = ((IMDBData) new Gson().fromJson(reader2, IMDBData.class)).getData();
					this.getCache().cache(videoId, entry);
					return entry;
				}
			} catch (Throwable e) {
				LOGGER.trace(videoId + " not found");
				this.getCache().cache(videoId, IMDBEntry.class);
				return null;
			}

		} else {
			return optionalIMDBEntry.get();
		}
	}

}
