package fr.pantheonsorbonne.cri.xes;

import java.time.Instant;

import fr.pantheonsorbonne.cri.model.stream4good.Lolomo;
import fr.pantheonsorbonne.cri.model.stream4good.UserData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.EventType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;

public class EventFactory extends XesFactory {

	public EventFactory(ObjectFactory factoryXes) {
		super(factoryXes);
	}

	public EventType getLolomoEvent(String lolomoType, String lolomoAssociatedContent, int lolomoRank,
			Instant timestamp, String userId,String lolomoCluster) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.NETFLIX_ASSET_TYPE, NETFLIX_ASSET_TYPE.LOLOMO));
		//event.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "lolomo-" + lolomoCluster));
		//event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "lolomo-" + lolomoCluster));
		//event.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "lolomo-" + lolomoRank));
		//event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "lolomo-" + lolomoRank));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "lolomo" ));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "lolomo"));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ORG_RESOURCE, userId));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.RESOURCE, userId));
		// event.getStringsAndDatesAndInts().add(attr("associated_content",
		// lolomoAssociatedContent));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.RANK, lolomoRank));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.TIMESTAMP, timestamp));

		return event;
	}

	public EventType getThumbnailEvent(String thumbnailId, int row, int col, Instant instant, String country,
			String userId) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.NETFLIX_ASSET_TYPE, NETFLIX_ASSET_TYPE.THUMBNAIL));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "thumbnail-"+country));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "thumbnail-"+country));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.COUNTRY,  country));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ORG_RESOURCE, userId));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.RESOURCE, userId));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ROW, row));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.COL, col));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.TIMESTAMP, instant));

		return event;
	}

	public EventType getWatchEvent(String videoId, long duration, Instant instant, String country, String userId) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.NETFLIX_ASSET_TYPE, NETFLIX_ASSET_TYPE.WATCH));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "watch-" + country));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "watch-" + country));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ORG_RESOURCE, userId));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.RESOURCE, userId));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.TIMESTAMP, instant));
		// event.getStringsAndDatesAndInts().add(attr("duration", instant));

		return event;
	}

	public EventType getStartSessionEvent(Instant creation_date, String userId) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.NETFLIX_ASSET_TYPE, NETFLIX_ASSET_TYPE.SESSION_START));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "start_session"));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "start_session"));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.TIMESTAMP, creation_date));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ORG_RESOURCE, userId));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.RESOURCE, userId));
		return event;

	}
	
	public EventType getFakeEndSessionEvent(Instant creation_date, String userId) {
		EventType event = factoryXes.createEventType();
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.NETFLIX_ASSET_TYPE, NETFLIX_ASSET_TYPE.SESSION_END));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "end_session"));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "end_session"));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.TIMESTAMP, creation_date));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.ORG_RESOURCE, userId));
		event.getStringsAndDatesAndInts().add(attr(XES_ATTR.RESOURCE, userId));
		return event;

	}
	
	

}