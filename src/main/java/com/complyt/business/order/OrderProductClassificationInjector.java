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

@AllArgsConstructor
@Getter
@Slf4j
public class OrderProductClassificationInjector implements OrderDataInjector<ProductClassification> {
    @NonNull
    private final Order order;

    @Override
    public Mono<Order> act(List<ProductClassification> productClassifications) {
        return Mono.fromCallable(() -> {

            List<Item> itemsWithRules = new ArrayList<>();
            for (Item item : order.getItems()) {

                for (ProductClassification productClassification : productClassifications) {

                    if (productClassification.getTaxCode().equals(item.getTaxCode())) {

                        for (JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules : productClassification.getJurisdictionalSalesTaxRules()) {

                            if (jurisdictionalSalesTaxRules.getAbbreviation().equals(order.getShippingAddress().getState())) {
                                itemsWithRules.add(item.withJurisdictionalSalesTaxRules(jurisdictionalSalesTaxRules));
                                break;
                            }
                        }
                        break;
                    }
                }
            }
            return order.withItems(itemsWithRules);
        });
    }
}
