package com.complyt.services;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import reactor.core.publisher.Mono;


public interface SalesTaxService {
    Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking, @NonNull Customer customer);

    Mono<Transaction> calculate(@NonNull Transaction transaction);
}
