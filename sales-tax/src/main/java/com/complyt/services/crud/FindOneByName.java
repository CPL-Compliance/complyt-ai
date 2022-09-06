package com.complyt.services.crud;

import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface FindOneByName<T> {
    Mono<T> findOneByName(@NonNull final String name);
}
