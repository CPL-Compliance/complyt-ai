package com.complyt.services;

import com.complyt.business.order.OrderJurisdictionalRulesInjector;
import com.complyt.business.order.OrderTangibleCategoryInjector;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.repositories.ProductClassificationRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

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
    public Mono<Order> setDataToOrder(Order order) {
        return Flux.fromIterable(order.getItems())
                .flatMap(item -> getClassification(item.getTaxCode()))
                .collectMap(ProductClassification::getTaxCode, productClassification -> productClassification)
                .flatMap(mapTaxCodesToClassifications -> injectJurisdictionalRules(order, mapTaxCodesToClassifications)
                        .flatMap(modifiedOrder -> injectTangibleCategories(modifiedOrder, mapTaxCodesToClassifications)));
    }

    @Override
    public Mono<Order> injectJurisdictionalRules(Order order, Map<String, ProductClassification> mapTaxCodesToClassifications) {
        OrderJurisdictionalRulesInjector injector = new OrderJurisdictionalRulesInjector(order);
        return injector.act(mapTaxCodesToClassifications);
    }

    private Mono<Order> injectTangibleCategories(Order order, Map<String, ProductClassification> mapTaxCodesToClassifications) {
        OrderTangibleCategoryInjector injector = new OrderTangibleCategoryInjector(order);
        return injector.act(mapTaxCodesToClassifications);
    }

    private Mono<ProductClassification> getClassification(String taxCode) {
        return findOneByTaxCode(taxCode);
    }
}
