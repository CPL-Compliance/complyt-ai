package io.complyt.services;

import io.complyt.domain.Address;
import reactor.core.publisher.Mono;

public interface ValidAddressService {
    Mono<Address> validateAddress(Address address);
}