package fr.pantheonsorbonne.cri.model.stream4good;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.NoSuchElementException;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

public abstract class LinkedResource {

	protected abstract List<Link> getResourceLinks();

	public WebTarget getLinkTarget(String string, Client client) {

		if (getResourceLinks() == null) {
			throw new NoSuchElementException(string);
		}
		try {
			URI uri = new URI(
					getResourceLinks().stream().filter(l -> string.equals(l.getRel()) || string.equals(l.getName()))
							.findFirst().orElseThrow().getHref());
			return client.target(uri);

		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}

	}

}
