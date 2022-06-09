package com.complyt.business.order;

import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@Getter
@Slf4j
public class OrderProductClassificationInjector implements OrderDataInjector<ProductClassification> {
    @NonNull
    private final Order order;

    @Override
    public Mono<Order> act(Map<String,ProductClassification> productClassifications) {
        return Mono.fromCallable(() -> {
            String state = order.getShippingAddress().getState();
            List<Item> itemsWithRules = new ArrayList<>();

            for(Item item: order.getItems()) {
                ProductClassification productClassification = productClassifications.get(item.getTaxCode());
                JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = productClassification.getJurisdictionalSalesTaxRules().get(state);
                itemsWithRules.add(item.withJurisdictionalSalesTaxRules(jurisdictionalSalesTaxRules));
            }
            return order.withItems(itemsWithRules);

        });
    }
}
