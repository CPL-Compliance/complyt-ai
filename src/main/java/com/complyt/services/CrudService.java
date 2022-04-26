package com.complyt.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CrudService<T, ID> {
    Mono<T> save(T object);
    Mono<T> findOneByName(String name);
    Flux<T> findByName(String name);
    Mono<T> findById(ID id);
    Flux<T> findAll();
}
