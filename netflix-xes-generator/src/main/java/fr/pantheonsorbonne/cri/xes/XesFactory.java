package fr.pantheonsorbonne.cri.xes;

import java.time.Instant;

import fr.pantheonsorbonne.cri.helper.Helper;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeDateType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeIntType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeStringType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ClassifierType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ExtensionType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.GlobalsType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;

public class XesFactory {

	public enum NETFLIX_ASSET_TYPE {
		LOLOMO, THUMBNAIL, WATCH, SESSION_START;
	}

	public enum XES_ATTR {

		TIMESTAMP("time:timestamp"), //
		CONCEPT("concept:name"), //
		ACTIVITY("Activity"), //
		RESOURCE("Resource"), //
		ORG_RESOURCE("org:resource"), //
		VIDEO_ID("VideoId"), //
		ROW("row"), //
		COL("col"), //
		RANK("rank"), //
		NETFLIX_ASSET_TYPE("netflixType");

		private final String xesName;

		XES_ATTR(String string) {
			xesName = string;
		}

		public String getXesName() {
			return xesName;
		}
	}

	public XesFactory(ObjectFactory factoryXes) {
		this.factoryXes = factoryXes;
	}

	protected final ObjectFactory factoryXes;

	protected AttributeType attr(XES_ATTR key, String value) {
		AttributeStringType attr = new AttributeStringType();
		attr.setKey(key.xesName);
		attr.setValue(value);
		return attr;
	}
	
	protected AttributeType attr(XES_ATTR key, NETFLIX_ASSET_TYPE value) {
		AttributeStringType attr = new AttributeStringType();
		attr.setKey(key.xesName);
		attr.setValue(value.toString());
		return attr;
	}

	protected AttributeType attr(XES_ATTR key, int value) {
		AttributeIntType attr = new AttributeIntType();
		attr.setKey(key.xesName);
		attr.setValue(value);
		return attr;
	}

	protected AttributeType attr(XES_ATTR key, Instant value) {
		AttributeDateType attr = new AttributeDateType();
		attr.setKey(key.xesName);
		attr.setValue(Helper.toDate(value));
		return attr;
	}

	public Log getLog() {
		Log log = this.factoryXes.createLog();
		{
			ExtensionType ext = this.factoryXes.createExtensionType();
			ext.setName("Concept");
			ext.setPrefix("concept");
			ext.setUri("http://code.deckfour.org/xes/concept.xesext");
			log.getExtensions().add(ext);
		}
		{
			ExtensionType ext = this.factoryXes.createExtensionType();
			ext.setName("Time");
			ext.setPrefix("time");
			ext.setUri("http://code.deckfour.org/xes/time.xesext");
			log.getExtensions().add(ext);
		}
		{
			ExtensionType ext = this.factoryXes.createExtensionType();
			ext.setName("Organizational");
			ext.setPrefix("org");
			ext.setUri("http://code.deckfour.org/xes/org.xesext");
			log.getExtensions().add(ext);
		}
		{
			GlobalsType global = this.factoryXes.createGlobalsType();
			global.setScope("event");
			global.getStringsAndDatesAndInts().add(attr(XES_ATTR.CONCEPT, "name"));
			global.getStringsAndDatesAndInts().add(attr(XES_ATTR.ORG_RESOURCE, "resource"));
			global.getStringsAndDatesAndInts().add(attr(XES_ATTR.TIMESTAMP, Instant.now()));
			global.getStringsAndDatesAndInts().add(attr(XES_ATTR.ACTIVITY, "string"));
			global.getStringsAndDatesAndInts().add(attr(XES_ATTR.RESOURCE, "string"));

			log.getGlobals().add(global);
		}

		{
			ClassifierType classifier = this.factoryXes.createClassifierType();
			classifier.setKeys("Activity");
			classifier.setName("Activity");
			log.getClassifiers().add(classifier);
		}

		{
			ClassifierType classifier = this.factoryXes.createClassifierType();
			classifier.setKeys("Activity");
			classifier.setName("activity classifier");
			log.getClassifiers().add(classifier);
		}

		return log;

	}

}
