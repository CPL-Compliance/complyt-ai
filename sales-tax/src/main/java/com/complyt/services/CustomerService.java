package com.complyt.services;

import com.complyt.domain.customer.Customer;
import com.complyt.services.crud.CrudService;
import com.complyt.services.crud.FindByName;
import com.complyt.services.crud.FindOneByName;
import lombok.NonNull;
import org.bson.types.ObjectId;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface CustomerService extends CrudService<Customer, String>, FindByName<Customer>, FindOneByName<Customer> {

    Mono<Customer> save(@NonNull Customer customer);

    Mono<Customer> update(@NonNull Customer customer);

    Mono<Customer> findOneByName(String name);

    Mono<Customer> findByExternalIdAndSource(String externalId, String source);

    Flux<Customer> findAll(int page, int size);

    Flux<Customer> findAllBySource(String source);

    Mono<Customer> findByComplytId(UUID complytId);

    Mono<Customer> findById(@NonNull ObjectId id);

    Mono<Customer> injectDataToExistingCustomer(Customer newCustomer, Customer originalCustomer);

    Mono<Customer> injectDataToNewCustomer(Customer customer);

    Mono<Customer> checkCustomerNotHavingComplytId(@NonNull final Customer newCustomer);

    Mono<Customer> checkComplytIdOfModifiedEqualsToOriginal(@NonNull final Customer modifiedCustomer, @NonNull final Customer originalCustomer);
}