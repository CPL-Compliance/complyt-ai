package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface ProductClassificationService extends CrudService<Order, String> {
    public Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode);
}
