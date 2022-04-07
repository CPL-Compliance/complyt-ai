package com.complyt.services;

import com.complyt.domain.Customer;
import com.complyt.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private CustomerRepository customerRepository;

    @Override
    public Mono<Customer> create(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Customer> findOneByName(String name) {
        return customerRepository.findOneByName(name);
    }

    @Override
    public Flux<Customer> findByName(String name) {
        return customerRepository.findByName(name);
    }

    @Override
    public Mono<Customer> findById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Flux<Customer> findAll() {
        return customerRepository.getAllCustomers();
    }

    @Override
    public Mono<Customer> save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }
}