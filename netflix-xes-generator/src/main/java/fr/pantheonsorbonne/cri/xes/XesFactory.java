package fr.pantheonsorbonne.cri.xes;

import java.time.Instant;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import fr.pantheonsorbonne.cri.App;
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
		attr.setValue(toDate(value));
		return attr;
	}

	static XMLGregorianCalendar toDate(Instant instant) {
		String dateTimeString = instant.toString();
		XMLGregorianCalendar date2;
		try {
			date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(dateTimeString);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
		return date2;
	}

}
