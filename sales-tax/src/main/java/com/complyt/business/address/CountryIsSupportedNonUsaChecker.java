package com.complyt.business.address;


import com.complyt.domain.transaction.Address;
import lombok.NonNull;


public interface CountryIsSupportedNonUsaChecker {
    static boolean isCountrySupportedNonUsaCountry(@NonNull Address address) {
        return isCountrySupportedNonUsaCountry(address.country().toUpperCase());
    }

    static boolean isCountrySupportedNonUsaCountry(@NonNull String country) {
        return SupportedNonUsCountries.nonUsaCountriesAbbreviations.containsKey(country.toUpperCase());
    }
}
