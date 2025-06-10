package com.complyt.business.address_validation;

import com.complyt.domain.transaction.ShippingAddress;
import reactor.core.publisher.Mono;

public interface AddressValidationWebClientWrapper<T> {
    Mono<T> validateAddress(ShippingAddress address);

    Mono<T> validateAddress(String city, String country, String county, String state, String region, String street, String zip,
                            boolean isPartial);
}
