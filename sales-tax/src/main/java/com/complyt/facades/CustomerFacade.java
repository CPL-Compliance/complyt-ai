package com.complyt.facades;

import com.complyt.domain.customer.Customer;
import com.complyt.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CustomerFacade {

    @Qualifier("customerServiceImpl")
    @NonNull
    private CustomerService customerService;

    public Mono<Customer> saveCustomer(@NonNull Customer customer) {
        return customerService.checkCustomerNotHavingComplytId(customer)
                .flatMap(customerService::injectDataToNewCustomer)
                .flatMap(customerService::save);
    }

    public Mono<Customer> updateIfModified(@NonNull Customer newCustomer, @NonNull Customer originalCustomer) {
        return originalCustomer.equals(newCustomer) ?
                Mono.just(newCustomer) : customerService.checkComplytIdOfModifiedEqualsToOriginal(newCustomer, originalCustomer)
                .flatMap(customerService::update);
    }

    public Flux<Customer> findByName(String name) {
        return customerService.findByName(name);
    }

    public Mono<Customer> findByExternalIdAndSource(String externalId, String source) {
        return customerService.findByExternalIdAndSource(externalId, source);
    }
    public Mono<Customer> findByComplytId( @NonNull UUID complytId) {
        return customerService.findByComplytId(complytId);
    }

    public Flux<Customer> getAll() {
        return customerService.findAll();
    }
    public Flux<Customer> getAllBySource(String source) {
        return customerService.findAllBySource(source);
    }

}
