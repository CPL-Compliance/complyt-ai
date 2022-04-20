package com.complyt.facades;

import com.complyt.domain.Customer;
import com.complyt.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class CustomerFacade {

    @Qualifier("customerServiceImpl")
    @NonNull
    private CustomerService customerService;

    public Customer save(Customer customer) {
        return customerService.save(customer);
    }

    public Mono<Customer> upsert(Customer customer) {
        return customerService.upsert(customer);
    }

    public Flux<Customer> findByName(String name) {
        return customerService.findByName(name);
    }

    public Mono<Customer> findByExternalId(String externalId) {
        return customerService.findByExternalId(externalId);
    }

    public Flux<Customer> getAllCustomers() {
        return customerService.findAll();
    }
}
