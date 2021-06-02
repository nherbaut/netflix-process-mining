package fr.pantheonsorbonne.cri.helper;

import java.time.Instant;
import java.time.ZoneId;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public final class Helper {
	
	public static Instant getInstant(XMLGregorianCalendar xgc) {
	
	return xgc.toGregorianCalendar()
            .toZonedDateTime()
            .withZoneSameLocal(ZoneId.of("Europe/Paris"))
            .toInstant();
	}

	public static XMLGregorianCalendar toDate(Instant instant) {
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
