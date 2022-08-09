package com.complyt.services;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import reactor.core.publisher.Mono;


public interface SalesTaxService {
    Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking);
    Mono<Transaction> injectCountyToTransactionAndCalculate(@NonNull Transaction transaction);
}
