package fr.pantheonsorbonne.cri;

import java.time.Instant;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.Lolomo;
import fr.pantheonsorbonne.ufr27.miage.model.xes.EventType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;

class EventFactory extends XesFactory {

	public EventFactory(ObjectFactory factoryXes) {
		super(factoryXes);
	}

	public EventType getLolomoEvent(Lolomo lolomo) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr("concept:name", "lolomo"));
		event.getStringsAndDatesAndInts().add(attr("org:resource", lolomo.getType()));
		event.getStringsAndDatesAndInts().add(attr("associated_content", lolomo.getAssociated_content()));
		event.getStringsAndDatesAndInts().add(attr("rank", lolomo.getRank()));
		event.getStringsAndDatesAndInts().add(attr("timestamp", lolomo.getTimestamp()));

		return event;
	}

	public EventType getThumbnailEvent(String thumbnailId, int row, int col, Instant instant, String country) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr("concept:name", "thumbnail-" + country));
		event.getStringsAndDatesAndInts().add(attr("org:resource", thumbnailId));
		event.getStringsAndDatesAndInts().add(attr("row", row));
		event.getStringsAndDatesAndInts().add(attr("col", col));
		event.getStringsAndDatesAndInts().add(attr("timestamp", instant));

		return event;
	}

	public EventType getWatchEvent(String videoId, long duration, Instant instant, String country) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr("concept:name", "watch-" + country));
		event.getStringsAndDatesAndInts().add(attr("org:resource", videoId));

		event.getStringsAndDatesAndInts().add(attr("timestamp", instant));
		event.getStringsAndDatesAndInts().add(attr("duration", instant));

		return event;
	}

}