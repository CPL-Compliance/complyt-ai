package com.complyt.business.address_validation;

import com.complyt.annotations.Generated;
import com.complyt.domain.Address;
import com.complyt.domain.common_rates.Scoring;
import com.complyt.domain.enums.FieldMatchType;
import com.complyt.domain.enums.FieldsMatchScore;
import com.complyt.domain.enums.MatchLevelType;
import com.complyt.domain.matched_address.MatchedAddressData;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Generated
@EqualsAndHashCode
public class StubAddressValidationWebClientWrapper implements AddressValidationWebClientWrapper<MatchedAddressData> {
    @Override
    public Mono<MatchedAddressData> validateAddress(Address address) {
        return validateAddress(address.city(), address.country(), address.county(),
                address.state(), address.street(), address.zip(), address.isPartial());
    }

    @Override
    public Mono<MatchedAddressData> validateAddress(String city, String country, String county, String state, String street, String zip, boolean isPartial) {
        Address address = new Address("Anchorage", "USA", "Anchorage",
                "Alaska", "751-2696 205 E Benson Blvd",
                "99501",false);
        Scoring scoring = new Scoring(MatchLevelType.EXCELLENT, 1, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));
        MatchedAddressData matchedAddressData = new MatchedAddressData(address, scoring);

        // for tests that check external flow in internal sales tax profile
        switch (state) {
            case "Colorado" -> {
                address = new Address("Englewood", "US", "Arapahoe", "Colorado", "street", "80112", true);
                return Mono.just(matchedAddressData.withAddress(address));
            }

            // for tests that get Unincorporated address
            case "West Virginia" -> {
                address = new Address("Englewood", "US", "MERCER", "West Virginia", "751-2696 205 E Benson Blvd", "24740-9669", true);
                return Mono.just(matchedAddressData.withAddress(address));
            }


            // for tests that get Internal Rates Tests with rate not found - external address
            case "Hawaii" -> {
                address = new Address("Anchorage", "USA", "Anchorage", "Hawaii", "751-2696 205 E Benson Blvd", "99501", false);
                return Mono.just(matchedAddressData.withAddress(address));
            }
        }

        return Mono.just(matchedAddressData);
    }
}