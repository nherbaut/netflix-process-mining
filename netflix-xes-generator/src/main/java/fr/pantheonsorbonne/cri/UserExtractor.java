package fr.pantheonsorbonne.cri;

import java.time.Instant;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.UserData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;

class UserExtractor extends TraceFactory {
	public UserExtractor(ObjectFactory factoryXes, UserData userData, Log log) {
		super(factoryXes, userData);
	}

	public TraceType getUserTrace() {

		TraceType trace = factoryXes.createTraceType();
		trace.getStringsAndDatesAndInts().add(attr("user_id", userData.getUser().getUser_id()));
		trace.getStringsAndDatesAndInts()
				.add(attr("user_creation_date", Instant.ofEpochMilli(userData.getUser().getCreation_date())));

		return trace;
	}

}