package io.complyt.business.address;

public interface CountryToStandardizedCountry {
    static String standardize(String country) {
        return CountryIsUsaChecker.isCountryUsa(country.toUpperCase()) ?
                UsaAbbreviations.usaAbbreviationsList.get(country.toUpperCase().trim()) : country;
    }
}