package com.complyt.facades;

import com.complyt.business.order.OrderProductClassificationInjector;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

import static java.util.stream.Collectors.toSet;


@Component
@AllArgsConstructor
public class OrderFacade {
    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    @Qualifier("salesTaxServiceImpl")
    @NonNull
    private SalesTaxService salesTaxService;

    @Qualifier("productClassificationServiceImpl")
    @NonNull
    private ProductClassificationService productClassificationService;

    public Mono<Order> save(Order order) {
        return orderService.save(order);
    }

    public Mono<Order> upsert(@NonNull String externalId, Order order) {
        return orderService.upsert(externalId, order);
    }

    public Mono<Order> update(@NonNull String externalId, Order order) {
        return orderService.update(externalId, order);
    }

    public Mono<Order> findByExternalId(String externalId) {
        return orderService.findByExternalId(externalId);
    }

    public Flux<Order> getAll() {
        return orderService.findAll();
    }

    public Mono<Order> updateSalesTax(String externalId) {
        return orderService
                .findByExternalId(externalId)
                .flatMap(order -> {
                    OrderProductClassificationInjector orderProductClassificationModifier = new OrderProductClassificationInjector(order);
                    Set<String> taxCodes = order.getItems().stream()
                            .map(item -> item.getTaxCode())
                            .collect(toSet());

                    Flux<ProductClassification> productClassificationFlux = productClassificationService.findByTaxCodes(taxCodes);

                    return orderProductClassificationModifier.act(productClassificationFlux);
                })
                .flatMap(order -> salesTaxService.getSalesTax(order.getShippingAddress(), order.getItems())
                        .map(order::withSalesTax))
                .flatMap(order -> orderService.update(externalId, order));
    }


    @SneakyThrows
    public Mono<ProductClassification> getClassification(String taxCode) {
        return productClassificationService.findOneByTaxCode(taxCode);
    }

    public Mono<Order> markAsCancelled(String orderId) {
        return orderService.markAsCancelled(orderId);
    }

}