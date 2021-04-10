package fr.pantheonsorbonne.ufr27.miage.model.stream4good;

import java.util.List;

public class Content extends LinkedResource {
	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getIdentifierId() {
		return identifierId;
	}

	public void setIdentifierId(String identifierId) {
		this.identifierId = identifierId;
	}

	public String getImdbId() {
		return imdbId;
	}

	public void setImdbId(String imdbId) {
		this.imdbId = imdbId;
	}

	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	String provider;
	String identifierId;
	String imdbId;
	List<Link> links;

	@Override
	protected List<Link> getResourceLinks() {
		return this.getLinks();
	}

}
