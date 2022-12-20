package com.complyt.services;

import com.complyt.business.dates_injection.ModifiedCustomerInternalDateInjector;
import com.complyt.business.dates_injection.NewCustomerInternalDateInjector;
import com.complyt.domain.customer.Customer;
import com.complyt.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
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

    public Mono<Customer> update(@NonNull Customer customer) {
        return customerRepository.findByExternalId(customer.getExternalId())
                .switchIfEmpty(customerRepository.save(customer))
                .map(createFunctionUpdateCustomer(customer))
                .flatMap(customerRepository::save);
    }

    public Customer injectDataToNewCustomer(Customer customer) {
        return new NewCustomerInternalDateInjector(customer).inject();
    }

    public Customer injectDataToModifiedCustomer(Customer modifiedCustomer, Customer originalCustomer) {
        Customer modifiedCustomerWithInternalTimeStamps = modifiedCustomer
                .withInternalTimeStamps(originalCustomer.getInternalTimeStamps());

        return new ModifiedCustomerInternalDateInjector(modifiedCustomerWithInternalTimeStamps).inject();
    }

    @Override
    public Mono<Customer> findByExternalId(String externalId) {
        return customerRepository.findByExternalId(externalId);
    }

    @Override
    public Flux<Customer> findByName(@NonNull String name) {
        return customerRepository.findByName(name);
    }

    @Override
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

    @Override
    public Mono<Customer> findById(@NonNull ObjectId id) {
        return customerRepository.findById(id);
    }

    private Function<Customer, Customer> createFunctionUpdateCustomer(final Customer customer) {
        return customerInfo -> customerInfo.withExternalId(customer.getExternalId())
                .withAddress(customer.getAddress())
                .withName(customer.getName());
    }
}