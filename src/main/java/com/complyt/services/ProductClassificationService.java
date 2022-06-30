package com.complyt.services;

import com.complyt.business.order.OrderProductClassificationInjector;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.NonNull;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductClassificationService extends CrudService<ProductClassification, String> {
    Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode);
    Flux<ProductClassification> getAll() ;

    Mono<Order> setJuresdictionalRules(OrderProductClassificationInjector orderProductClassificationInjector);
}