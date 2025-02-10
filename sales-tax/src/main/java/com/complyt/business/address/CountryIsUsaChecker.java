package com.complyt.business.address;


import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingAddress;
import lombok.NonNull;


public interface CountryIsUsaChecker {
    static boolean isCountryUsa(@NonNull ShippingAddress address) {
        return isCountryUsa(address.country().toUpperCase());
    }

    static boolean isCountryUsa(@NonNull String country) {
        return UsaAbbreviations.usaAbbreviationsList.containsKey(country.toUpperCase());
    }
}