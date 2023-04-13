package com.complyt.services;

import com.complyt.domain.Address;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface CountyService {
    Mono<String> findByAddress(@NonNull Address address);
}
