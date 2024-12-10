package com.complyt.business.address_validation;

import com.complyt.domain.transaction.Address;
import reactor.core.publisher.Mono;

public interface AddressValidationWebClientWrapper<T> {
    Mono<T> validateAddress(Address address);

    Mono<T> validateAddress(String city, String country, String county, String state, String street, String zip,
                            boolean isPartial);
}
