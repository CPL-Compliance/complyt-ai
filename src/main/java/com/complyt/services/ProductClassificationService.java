package com.complyt.services;

import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductClassificationService extends CrudService<Transaction, String> {
    Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode);
    Flux<ProductClassification> getAll() ;
}