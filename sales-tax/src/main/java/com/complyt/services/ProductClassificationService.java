package com.complyt.services;

import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Mono;


public interface ProductClassificationService extends CrudService<ProductClassification, String> {
    Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode);

    Mono<Transaction> getTransactionWithRelevantProductClassificationData(Transaction transaction);
}