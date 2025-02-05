package com.complyt.business.address;

import com.complyt.v1.model.AddressDto;
import lombok.NonNull;


public interface CountryIsUsaChecker {
    static boolean isCountryUsa(@NonNull AddressDto address) {
        return isCountryUsa(address.country().toUpperCase());
    }

    static boolean isCountryUsa(@NonNull String country) {
        return UsaAbbreviations.usaAbbreviationsList.containsKey(country.toUpperCase());
    }
}