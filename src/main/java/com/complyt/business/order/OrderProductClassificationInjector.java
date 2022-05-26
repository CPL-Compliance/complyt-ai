package com.complyt.business.order;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
public class OrderProductClassificationInjector implements OrderDataInjector<ProductClassification> {
    @NonNull
    private final Order order;

    @Override
    public Mono<Order> act(Flux<ProductClassification> productClassificationFlux) {
        return null;
    }
}
