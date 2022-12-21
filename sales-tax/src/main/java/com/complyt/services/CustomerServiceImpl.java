package com.complyt.services;

import com.complyt.business.timestamps_injection.ExistingCustomerInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewCustomerInternalTimestampsInjector;
import com.complyt.domain.customer.Customer;
import com.complyt.repositories.CustomerRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
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
        Customer customerWithInjectedData = injectDataToNewCustomer(customer);
        return customerRepository.save(customerWithInjectedData);
    }

    public Mono<Customer> update(@NonNull Customer newCustomer) {
        return customerRepository.findByExternalId(newCustomer.getExternalId())
                .switchIfEmpty(Mono.error(new NotFoundException("No customer with externalId " + newCustomer.getExternalId())))
                .map(originalCustomer -> {
                    Customer customerWithInjectedData = injectDataToExistingCustomer(newCustomer, originalCustomer);
                    return createFunctionUpdateCustomer(customerWithInjectedData).apply(originalCustomer);
                })
                .flatMap(customerRepository::save);
    }

    public Customer injectDataToNewCustomer(Customer customer) {
        return new NewCustomerInternalTimestampsInjector(customer).inject();
    }

    public Customer injectDataToExistingCustomer(Customer newCustomer, Customer originalCustomer) {
        Customer existingCustomerWithInternalTimeStamps = newCustomer
                .withInternalTimeStamps(originalCustomer.getInternalTimeStamps());

        return new ExistingCustomerInternalTimestampsInjector(existingCustomerWithInternalTimeStamps).inject();
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
        return customerInfo -> customerInfo
                .withExternalId(customer.getExternalId())
                .withAddress(customer.getAddress())
                .withName(customer.getName())
                .withCustomerType(customer.getCustomerType())
                .withInternalTimeStamps(customer.getInternalTimeStamps())
                .withExternalTimeStamps(customer.getExternalTimeStamps());
    }
}