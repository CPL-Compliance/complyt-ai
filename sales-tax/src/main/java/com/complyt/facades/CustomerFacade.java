package com.complyt.facades;

import com.complyt.domain.customer.Customer;
import com.complyt.services.CustomerServiceImpl;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@AllArgsConstructor
public class CustomerFacade {

    @Qualifier("customerServiceImpl")
    @NonNull
    private CustomerServiceImpl customerService;

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

    public Mono<Customer> findByComplytId(@NonNull UUID complytId) {
        return customerService.findByComplytId(complytId);
    }

    public Flux<Customer> getAll(int page, int size, Map<String, String> filterMap, String sortOrder, String sortBy) {
        return customerService.findAll(page, size, filterMap, sortOrder, sortBy);
    }

    public Flux<Customer> findCustomers(Optional<String> email, Optional<String> source) {
        return customerService.findCustomers(email, source);
    }


    public Flux<Customer> getAllBySource(String source) {
        return customerService.findAllBySource(source);
    }

}
