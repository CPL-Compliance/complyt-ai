package com.complyt.services;

import com.complyt.business.complyt_id.CustomerComplytIdHandler;
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

import java.util.UUID;
import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @NonNull
    private CustomerRepository customerRepository;

    @NonNull
    private CustomerComplytIdHandler customerComplytIdHandler;

    @Override
    public Mono<Customer> save(@NonNull Customer customer) {
        return customerRepository.save(customer);
    }

    public Mono<Customer> update(@NonNull Customer newCustomer) {
        return customerRepository.findByExternalId(newCustomer.getExternalId(), newCustomer.getSource())
                .switchIfEmpty(Mono.error(new NotFoundException("No customer with externalId " + newCustomer.getExternalId())))
                .flatMap(originalCustomer ->
                        injectDataToExistingCustomer(newCustomer, originalCustomer)
                                .map(customerWithInjectedData -> createFunctionUpdateCustomer(customerWithInjectedData)
                                        .apply(originalCustomer)
                                ))
                /*.map(originalCustomer -> {
                    Customer customerWithInjectedData = injectDataToExistingCustomer(newCustomer, originalCustomer);
                    return createFunctionUpdateCustomer(customerWithInjectedData).apply(originalCustomer);
                })*/
                .flatMap(customerRepository::save);
    }

    @Override
    public Mono<Customer> injectDataToNewCustomer(Customer customer) {
        return Mono.just(customer)
                .map(customerComplytIdHandler::insertComplytIdToNew)
                .map(NewCustomerInternalTimestampsInjector::new)
                .map(NewCustomerInternalTimestampsInjector::inject);
    }

    @Override
    public Mono<Customer> checkCustomerNotHavingComplytId(@NonNull Customer newCustomer) {
        return customerComplytIdHandler.isNewDontHaveComplytId(newCustomer)
                .switchIfEmpty(Mono.error(new NotFoundException("cannot insert new transaction with complyt id")));
    }

    public Mono<Customer> checkComplytIdOfModifiedEqualsToOriginal(@NonNull final Customer modifiedCustomer, @NonNull final Customer originalCustomer) {
        return customerComplytIdHandler.isComplytIdOfUpdatedEqualsToOld(modifiedCustomer, originalCustomer)
                .switchIfEmpty(Mono.error(new NotFoundException("modified and original customer's complytIds not equal")));
    }

    @Override
    public Mono<Customer> injectDataToExistingCustomer(Customer newCustomer, Customer originalCustomer) {
        return Mono.just(newCustomer).map(customer -> customer
                        .withInternalTimestamps(originalCustomer.getInternalTimestamps()))
                .map(ExistingCustomerInternalTimestampsInjector::new)
                .map(ExistingCustomerInternalTimestampsInjector::inject);
    }

    @Override
    public Mono<Customer> findByExternalId(String externalId, String source) {
        return customerRepository.findByExternalId(externalId, source);
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
    public Flux<Customer> findAllBySource(String source) {
        return customerRepository.findAllBySource(source);
    }

    @Override
    public Mono<Customer> findByComplytId(UUID complytId) {
        return customerRepository.findByComplytId(complytId);
    }

    @Override
    public Mono<Customer> findById(@NonNull ObjectId id) {
        return customerRepository.findById(id);
    }

    private Function<Customer, Customer> createFunctionUpdateCustomer(final Customer customer) {
        return customerInfo ->
                new Customer(
                        customerInfo.getComplytId(), customerInfo.getId(), customer.getExternalId(),
                        customer.getSource(), customer.getName(), customer.getAddress(),
                        customerInfo.getTenantId(), customer.getCustomerType(),
                        customer.getInternalTimestamps(), customer.getExternalTimestamps()
                );
    }
}