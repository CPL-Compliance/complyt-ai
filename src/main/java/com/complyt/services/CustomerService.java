package com.complyt.services;

import com.complyt.domain.Customer;
import com.complyt.services.crud.CrudService;
import com.complyt.services.crud.FindByName;
import com.complyt.services.crud.FindOneByName;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService extends CrudService<Customer, String>, FindByName<Customer>, FindOneByName<Customer> {
    Mono<Customer> save(@NonNull Customer customer);

    Mono<Customer> upsert(@NonNull Customer customer);

    Mono<Customer> findOneByName(String name);

    Mono<Customer> findByExternalId(String externalId);

    Flux<Customer> findAll();
}