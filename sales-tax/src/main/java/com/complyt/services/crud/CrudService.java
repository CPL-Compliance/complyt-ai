package com.complyt.services.crud;

import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudService<T, ID> {
    Mono<T> save(T object);

    Mono<T> findById(@NonNull final ID id);

    Flux<T> findAll(int page, int size);
}