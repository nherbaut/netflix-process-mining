package fr.pantheonsorbonne.cri.model.stream4good;

import java.util.List;

public class ClassificationData {
	List<ClassificationItem> countries;
	List<ClassificationItem> lolomos;

	public List<ClassificationItem> getLolomos() {
		return lolomos;
	}

	public void setLolomos(List<ClassificationItem> lolomos) {
		this.lolomos = lolomos;
	}

	public List<ClassificationItem> getCountries() {
		return countries;
	}

	public void setCountries(List<ClassificationItem> countries) {
		this.countries = countries;
	}

}
