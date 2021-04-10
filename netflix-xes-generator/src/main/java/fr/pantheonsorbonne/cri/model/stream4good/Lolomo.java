package fr.pantheonsorbonne.cri.model.stream4good;

import java.time.Instant;

public class Lolomo {
	public int getRank() {
		return rank;
	}
	public void setRank(int rank) {
		this.rank = rank;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getFull_text_description() {
		return full_text_description;
	}
	public void setFull_text_description(String full_text_description) {
		this.full_text_description = full_text_description;
	}
	public Instant getTimestamp() {
		return Instant.ofEpochSecond(timestamp);
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	int rank;
	String type;
	String full_text_description;
	public String getAssociated_content() {
		return associated_content;
	}
	public void setAssociated_content(String associated_content) {
		this.associated_content = associated_content;
	}
	long timestamp;
	String associated_content;
}
