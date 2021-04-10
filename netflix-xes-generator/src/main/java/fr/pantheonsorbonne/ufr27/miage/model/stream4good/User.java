package fr.pantheonsorbonne.ufr27.miage.model.stream4good;

import java.util.ArrayList;
import java.util.List;

public class User {
	public long getCreation_date() {
		return creation_date;
	}

	public void setCreation_date(long creation_date) {
		this.creation_date = creation_date;
	}

	public String getCreation_date_human() {
		return creation_date_human;
	}

	public void setCreation_date_human(String creation_date_human) {
		this.creation_date_human = creation_date_human;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}


	private long creation_date;
	private String creation_date_human;
	private String user_id;

	
}
