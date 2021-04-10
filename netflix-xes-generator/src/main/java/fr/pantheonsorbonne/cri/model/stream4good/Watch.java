package fr.pantheonsorbonne.cri.model.stream4good;

import java.time.Instant;

public class Watch {
	public String getVideo_id() {
		return video_id;
	}

	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}

	public Instant getTimestamp() {
		return Instant.ofEpochSecond(timestamp);
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getDuration_seconds() {
		try {
			return Long.parseLong(duration_seconds);
		} catch (NumberFormatException nfe) {
			return -1;
		}
	}

	String video_id;
	long timestamp;

	String duration_seconds;

}
