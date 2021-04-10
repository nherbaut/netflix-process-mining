package fr.pantheonsorbonne.cri;

import fr.pantheonsorbonne.ufr27.miage.model.stream4good.UserData;
import fr.pantheonsorbonne.ufr27.miage.model.xes.ObjectFactory;

public abstract class TraceFactory extends XesFactory {

	protected final UserData userData;

	public TraceFactory(ObjectFactory factoryXes, UserData userData) {
		super(factoryXes);

		this.userData = userData;

	}

}
