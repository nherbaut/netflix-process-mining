package fr.pantheonsorbonne.cri;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import fr.pantheonsorbonne.cri.model.stream4good.Country;
import fr.pantheonsorbonne.cri.model.stream4good.CountryData;

public class CountryMapSingleton {

	private static final Map<String, String> countryMap;
	static {


		try (Reader readerCountryData = new InputStreamReader(
				CountryMapSingleton.class.getResourceAsStream("/countries.json"))) {
			CountryData countryData = new Gson().fromJson(readerCountryData, CountryData.class);
			countryMap = countryData.getCountries().stream()
					.collect(Collectors.toMap(Country::getName, c -> c.getCluster()));

		} catch (IOException e) {
			throw new RuntimeException(e);
		}

	}

	public static String getCluster(String countryName) {
		return countryMap.getOrDefault(countryName, "Other");
	}

}
