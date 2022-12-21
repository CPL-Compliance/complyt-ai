package com.complyt.facades;

import com.complyt.domain.customer.Customer;
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

    public Mono<Customer> saveCustomer(@NonNull Customer customer) {
        return customerService.save(customer);
    }

    public Mono<Customer> updateIfModified(@NonNull Customer newCustomer, @NonNull Customer originalCustomer) {
        return originalCustomer.equals(newCustomer) ?
                Mono.just(newCustomer) : customerService.update(newCustomer);
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
