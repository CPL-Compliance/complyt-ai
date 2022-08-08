package com.complyt.services;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.SalesTaxTracking;
import lombok.NonNull;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesTaxService {
    float calculateSalesTaxAmount(List<Item> items);
    Mono<Transaction> calculate(Transaction transaction);
    Mono<Transaction> handleSalesTaxCalculation(@NonNull Transaction transaction, @NonNull SalesTaxTracking salesTaxTracking);
}
