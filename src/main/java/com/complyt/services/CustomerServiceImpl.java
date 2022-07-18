package com.complyt.services;

import com.complyt.domain.Customer;
import com.complyt.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @NonNull
    private CustomerRepository customerRepository;

    @Override
    public Mono<Customer> save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Customer> upsert(@NonNull Customer customer) {
        return customerRepository.findByExternalId(customer.getExternalId())
                .switchIfEmpty(customerRepository.save(customer))
                .map(createUpdateCustomerFunction(customer))
                .flatMap(customerRepository::save);
    }

    @Override
    public Mono<Customer> findByExternalId(String externalId) {
        return customerRepository.findByExternalId(externalId);
    }

    @Override
    public Flux<Customer> findByName(@NonNull String name) {
        return customerRepository.findByName(name);
    }

    public Mono<Customer> findOneByName(@NonNull String name) {
        return customerRepository.findOneByName(name);
    }

    @Override
    public Mono<Customer> findById(@NonNull String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Flux<Customer> findAll() {
        return customerRepository.findAll();
    }

    private Function<Customer, Customer> createUpdateCustomerFunction(@NonNull final Customer customer) {
        return customerInfo -> customerInfo.withExternalId(customer.getExternalId())
                .withAddress(customer.getAddress())
                .withName(customer.getName());
    }
}