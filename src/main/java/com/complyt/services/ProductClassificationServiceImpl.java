package com.complyt.services;

import com.complyt.business.order.OrderProductClassificationInjector;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.repositories.ProductClassificationRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class ProductClassificationServiceImpl implements ProductClassificationService {

    @NonNull
    private ProductClassificationRepository productClassificationRepository;

    @Override
    public Mono<ProductClassification> save(ProductClassification object) {
        throw new UnsupportedOperationException("save isn't implemented");
    }

    @Override
    public Mono<ProductClassification> findOneByName(@NonNull String name) {
        throw new UnsupportedOperationException("findOneByName isn't implemented");
    }

    @Override
    public Flux<ProductClassification> findByName(@NonNull String name) {
        throw new UnsupportedOperationException("findByName isn't implemented");
    }

    @Override
    public Mono<ProductClassification> findById(@NonNull String id) {
        throw new UnsupportedOperationException("findById isn't implemented");
    }

    @Override
    public Flux<ProductClassification> findAll() {
        throw new UnsupportedOperationException("findAll isn't implemented");
    }

    @Override
    public Mono<ProductClassification> findOneByTaxCode(@NonNull String taxCode) {
        return productClassificationRepository.findOneByTaxCode(taxCode);
    }

    public Flux<ProductClassification> getAll() {
        return productClassificationRepository.findAll();
    }

    @Override
    public Mono<Order> setJurisdictionalRules(OrderProductClassificationInjector orderProductClassificationInjector) {
        return injectRulesToOrderItems().apply(orderProductClassificationInjector);
    }

    private Function<OrderProductClassificationInjector, Mono<Order>> injectRulesToOrderItems() {
        return orderProductClassificationInjector -> Flux.fromIterable(orderProductClassificationInjector.getOrder().getItems())
                .flatMap(item -> getClassification(item.getTaxCode()))
                .collectMap(productClassification -> productClassification.getTaxCode(), productClassification -> productClassification)
                .flatMap(orderProductClassificationInjector::act);
    }

    private Mono<ProductClassification> getClassification(String taxCode) {
        log.debug("Searching for product classification for tax code : " + taxCode);
        return findOneByTaxCode(taxCode);
    }
}
