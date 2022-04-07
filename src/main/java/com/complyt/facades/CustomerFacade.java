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

    public Mono<Customer> createCustomer(Customer customer) {
        return customerService.create(customer);
    }

    public Flux<Customer> findByName(String name) {
        return customerService.findByName(name);
    }

    public Flux<Customer> getAllCustomers() {
        return customerService.findAll();
    }
}
