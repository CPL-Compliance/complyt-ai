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
    public Customer save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Customer> upsert(@NonNull Customer customer){
        return customerRepository.upsert(customer);
    }

    public Mono<Customer> findOneByName(@NonNull String name) {
        return customerRepository.findOneByName(name);
    }

    @Override
    public Flux<Customer> findByName(@NonNull String name) {
        return customerRepository.findByName(name);
    }

    @Override
    public Mono<Customer> findById(@NonNull String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Flux<Customer> findAll() {
        return customerRepository.getAllCustomers();
    }

}