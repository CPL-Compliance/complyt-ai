package com.complyt.business.address_validation;

import com.complyt.annotations.Generated;
import com.complyt.domain.transaction.Address;
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
        Address address = new Address("Acampo", "USA", "San Joaquin",
                "California", "7498 N Remington Ave",
                "95220", null, false);

        return Mono.just(address);
    }
}