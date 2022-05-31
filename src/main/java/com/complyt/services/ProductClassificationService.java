package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

public interface ProductClassificationService extends CrudService<Order, String> {
    Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode);
    Flux<ProductClassification> findByTaxCodes(Set<String> taxCodes);
    Flux<ProductClassification> getAll() ;
}