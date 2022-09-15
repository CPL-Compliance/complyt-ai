package com.complyt.business.data_injector;

import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class TransactionCustomerInjector implements TransactionDataInjector<Customer> {

    @NonNull
    private Transaction transaction;

    @Override
    public Mono<Transaction> inject(@NonNull Customer customer) {
        return Mono.just(transaction.withCustomer(customer));
    }
}
