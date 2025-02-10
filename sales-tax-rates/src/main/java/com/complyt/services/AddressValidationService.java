package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.matched_address.MatchedAddressData;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface AddressValidationService {
    Mono<MatchedAddressData> validate(@NonNull Address address);
}
