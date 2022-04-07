package com.complyt.services;

import com.complyt.domain.Customer;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService extends CrudService<Customer, String> {
    Mono<Customer> save(@NonNull Customer customer);

    Mono<Customer> upsert(@NonNull Customer customer);

    Mono<Customer> findOneByName(String name);

    Flux<Customer> findAll();
}