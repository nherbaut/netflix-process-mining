package fr.pantheonsorbonne.cri.primespace;

import java.time.Instant;

import fr.pantheonsorbonne.cri.model.stream4good.Session;
import fr.pantheonsorbonne.cri.model.stream4good.UserData;
import fr.pantheonsorbonne.cri.xes.TraceFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.Log;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;
import fr.pantheonsorbonne.ufr27.miage.model.xes.TraceType;

class UserExtractor extends TraceFactory {
	private Session session;

	public UserExtractor(ObjectFactory factoryXes, Session session, Log log) {
		super(factoryXes, session.getUserData());
		this.session=session;
	}

	public TraceType getUserTrace() {

		TraceType trace = factoryXes.createTraceType();
		trace.getStringsAndDatesAndInts().add(attr(XES_ATTR.ORG_RESOURCE, session.getSession_id()));
		trace.getStringsAndDatesAndInts()
				.add(attr(XES_ATTR.TIMESTAMP, Instant.ofEpochMilli(userData.getUser().getCreation_date())));

		return trace;
	}

}