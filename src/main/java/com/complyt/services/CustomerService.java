package com.complyt.services;

import com.complyt.domain.Customer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CustomerService extends CrudService<Customer, String> {
    Mono<Customer> create(Customer customer);

    Mono<Customer> findOneByName(String name);

    Flux<Customer> findAll();
}