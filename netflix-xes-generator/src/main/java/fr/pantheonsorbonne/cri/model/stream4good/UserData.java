package fr.pantheonsorbonne.cri.model.stream4good;

import java.util.ArrayList;
import java.util.List;

public class UserData {
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	User user;
	List<Link> links;
	List<Session> sessions;
	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

}
