package com.complyt.services;

import com.complyt.domain.Address;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AddressValidationService {
    Mono<Address> validate(@NonNull Address address);
}
