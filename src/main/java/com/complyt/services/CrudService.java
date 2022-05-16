package com.complyt.services;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudService<T, ID> {
    Mono<T> save(T object);
    Mono<T> findOneByName(@NonNull final String name);
    Flux<T> findByName(@NonNull final String name);
    Mono<T> findById(@NonNull final ID id);
    Flux<T> findAll();
}
