package io.complyt.services;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import reactor.core.publisher.Mono;

public interface ValidAddressService {
    Mono<ValidatedAddress> validateAddress(Address address);
    Mono<CachedAddressData> resolveAddress(Address address);
}