package com.complyt.business.address;


import com.complyt.domain.transaction.Address;
import lombok.NonNull;


public interface CountryIsUsaChecker {
    static boolean isCountryUsa(@NonNull Address address) {
        return isCountryUsa(address.country().toUpperCase());
    }

    static boolean isCountryUsa(@NonNull String country) {
        return UsaAbbreviations.usaAbbreviationsList.contains(country.toUpperCase());
    }
}
