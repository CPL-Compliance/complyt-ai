package io.complyt.business.address_aligner;

import io.complyt.business.address.CountryIsUsaChecker;
import io.complyt.business.address.CountryStandardizedMap;
import io.complyt.business.address.UsaAbbreviations;
import io.complyt.business.collection_fetcher.StateMap;
import io.complyt.domain.Address;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ShippingAddressAligner implements AddressAligner {
    @Override
    public Address alignForOutsource(@NonNull Address address) {
        boolean isUSA = CountryIsUsaChecker.isCountryUsa(address.country());

        String country = isUSA
                ? UsaAbbreviations.usaAbbreviationsList.getOrDefault(address.country(), address.country())
                : address.country(); // Full USA abbreviation gets the highest score

        String state = isUSA
                ? StateMap.statesToStandartizedState.getOrDefault(address.state(), address.state())
                : address.region(); // Here accepts only the state as a query parameter; if the location is global, the region field is used instead.

        return address.withCountry(country)
                .withState(state);
    }

    @Override
    public Address alignGlobalAddress(@NonNull Address address) {
        boolean isUSA = CountryIsUsaChecker.isCountryUsa(address.country());

        return isUSA ? address : address.withCountry(CountryStandardizedMap.standardize(address.country())); // Standardize for find matter in global collection
    }
}