package fr.pantheonsorbonne.cri;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;

import fr.pantheonsorbonne.cri.model.stream4good.ClassificationItem;
import fr.pantheonsorbonne.cri.model.stream4good.ClassificationData;

public class ClassificationSingleton {

	private static final Map<String, String> countryMap;
	private static final Map<String, String> lolomoMap;
	static {

		try (Reader readerClassificationData = new InputStreamReader(
				ClassificationSingleton.class.getResourceAsStream("/classification.json"))) {
			ClassificationData classificationData = new Gson().fromJson(readerClassificationData,
					ClassificationData.class);
			countryMap = classificationData.getCountries().stream()
					.collect(Collectors.toMap(ClassificationItem::getName, c -> c.getCluster()));
			lolomoMap = classificationData.getLolomos().stream()
					.collect(Collectors.toMap(ClassificationItem::getName, c -> c.getCluster()));
			System.out.println(lolomoMap);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}

	}

	public static String getCountryCluster(String countryName) {
		return countryMap.getOrDefault(countryName, "Other");
	}

	public static String getLolomoCluster(String lolomotype) {
		return lolomoMap.getOrDefault(lolomotype, "Other");
	}

}
