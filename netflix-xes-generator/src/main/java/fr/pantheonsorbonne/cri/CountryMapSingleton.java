package fr.pantheonsorbonne.cri;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import fr.pantheonsorbonne.cri.model.stream4good.Country;
import fr.pantheonsorbonne.cri.model.stream4good.CountryData;

public class CountryMapSingleton {

	private static final Map<String, String> countryMap;
	static {
		FileReader readerCountryData;
		try {
			readerCountryData = new FileReader(
					"/home/nherbaut/workspace/netflix-process-mining/netflix-xes-generator/src/main/resources/countries.json");
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		CountryData countryData = new Gson().fromJson(readerCountryData, CountryData.class);
		countryMap = countryData.getCountries().stream()
				.collect(Collectors.toMap(Country::getName, c -> c.getCluster()));
	}

	public static String getCluster(String countryName) {
		return countryMap.getOrDefault(countryName, "Other");
	}

}
