package fr.pantheonsorbonne.cri.model.stream4good;

import java.util.List;

public class IMDBEntry {

	public List<Country> getCountries() {
		return countries;
	}

	public void setCountries(List<Country> countries) {
		this.countries = countries;
	}

	public List<Genre> getGenres() {
		return genres;
	}

	public void setGenres(List<Genre> genres) {
		this.genres = genres;
	}

	List<Country> countries;
	List<Genre> genres;

	public class IMDBDataFields {
		String identifierId;
		String imdbId;
		String year;

		public String getIdentifierId() {
			return identifierId;
		}

		public void setIdentifierId(String identifierId) {
			this.identifierId = identifierId;
		}

		public String getImdbId() {
			return imdbId;
		}

		public void setImdbId(String imdbId) {
			this.imdbId = imdbId;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}
	}

	public class Country {
		public int getCountryId() {
			return countryId;
		}

		public void setCountryId(int countryId) {
			this.countryId = countryId;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		int countryId;
		String country;

	}

	public class Genre {
		public int getGenreId() {
			return genreId;
		}

		public void setGenreId(int genreId) {
			this.genreId = genreId;
		}

		public String getGenre() {
			return genre;
		}

		public void setGenre(String genre) {
			this.genre = genre;
		}

		int genreId;
		String genre;
	}

}
