package com.complyt.services;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.services.crud.CrudService;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ProductClassificationService extends CrudService<ProductClassification, String> {
    Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode);
    Mono<Order> getOrderWithRelevantProductClassificationData(Order order);
}