package fr.pantheonsorbonne.cri;

import java.time.Instant;

import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeDateType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeIntType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeStringType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.AttributeType;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;

public class XesFactory {

	public XesFactory(ObjectFactory factoryXes) {
		this.factoryXes = factoryXes;
	}

	protected final ObjectFactory factoryXes;

	protected AttributeType attr(String key, String value) {
		AttributeStringType attr = new AttributeStringType();
		attr.setKey(key);
		attr.setValue(value);
		return attr;
	}

	protected AttributeType attr(String key, int value) {
		AttributeIntType attr = new AttributeIntType();
		attr.setKey(key);
		attr.setValue(value);
		return attr;
	}

	protected AttributeType attr(String key, Instant value) {
		AttributeDateType attr = new AttributeDateType();
		attr.setKey(key);
		attr.setValue(App.toDate(value));
		return attr;
	}

}
