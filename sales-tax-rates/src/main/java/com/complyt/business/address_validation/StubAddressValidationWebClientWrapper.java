package com.complyt.business.address_validation;

import com.complyt.annotations.Generated;
import com.complyt.domain.Address;
import lombok.EqualsAndHashCode;
import reactor.core.publisher.Mono;

@Generated
@EqualsAndHashCode
public class StubAddressValidationWebClientWrapper implements AddressValidationWebClientWrapper<Address> {
    @Override
    public Mono<Address> validateAddress(Address address) {
        return validateAddress(address.city(), address.country(), address.county(),
                address.state(), address.street(), address.zip(), address.isPartial());
    }

    @Override
    public Mono<Address> validateAddress(String city, String country, String county, String state, String street, String zip, boolean isPartial) {

        // for tests that check external flow in internal sales tax profile
        if (state.equals("Colorado"))
            return Mono.just(new Address("Englewood", "US", "Arapahoe", "Colorado", "street", "80112", true));

        // for tests that get Unincorporated address
        if (state.equals("West Virginia"))
            return Mono.just(new Address("Englewood", "US", "MERCER", "West Virginia", "751-2696 205 E Benson Blvd", "24740-9669", true));

        // for tests that get Internal Rates Tests with rate not found - external address
        if (state.equals("Hawaii"))
            return Mono.just(new Address("Anchorage", "USA", "Anchorage", "Hawaii", "751-2696 205 E Benson Blvd", "99501",false));

        Address address = new Address("Anchorage", "USA", "Anchorage",
                "Alaska", "751-2696 205 E Benson Blvd",
                "99501",false);

        return Mono.just(address);
    }
}