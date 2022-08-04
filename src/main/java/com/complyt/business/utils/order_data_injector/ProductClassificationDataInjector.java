package com.complyt.business.utils.order_data_injector;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.Map;

@Getter
@AllArgsConstructor
@Slf4j
public class ProductClassificationDataInjector implements OrderDataInjector<Map<String, ProductClassification>> {

    @NonNull
    private final Order order;

    @Override
    public Mono<Order> inject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        return new OrderJurisdictionalRulesInjector(order)
                .inject(mapTaxCodesToClassifications)
                .map(orderWithRules -> new OrderTangibleCategoryInjector(orderWithRules))
                .flatMap(orderTangibleCategoryInjector -> orderTangibleCategoryInjector.inject(mapTaxCodesToClassifications));
    }
}
