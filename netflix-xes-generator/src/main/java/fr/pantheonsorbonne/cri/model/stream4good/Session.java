package fr.pantheonsorbonne.cri.model.stream4good;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.WebTarget;

public class Session extends LinkedResource{
	public String getSession_id() {
		return session_id;
	}

	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}

	public long getCreation_data() {
		return creation_date;
	}

	public void setCreation_data(long creation_data) {
		this.creation_date = creation_data;
	}

	public List<Link> getResourceLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	private String session_id;
	private long creation_date;
	private List<Link> links;

	
}
