package com.complyt.business.order;

import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Slf4j
public class OrderProductClassificationInjector implements OrderDataInjector<ProductClassification> {
    @NonNull
    private final Order order;

    /** finds each of the order's item's  jurisdictional Sales Tax Rules and injects it to the item
     * @param mapTaxCodesToClassifications - hashmap of tax code to its product classification representation
     * @return - order with items with the corresponding jurisdictional Sales Tax Rules in each one of them
     */
    @Override
    public Mono<Order> act(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        return Mono.fromCallable(() -> {
            log.info("Setting jurisdictional sales tax rules to order's items");
            String state = order.getShippingAddress().getState();
            List<Item> itemsWithRules = new ArrayList<>();
            Item itemWithRules;
            for (Item item : order.getItems()) {
                ProductClassification productClassification = mapTaxCodesToClassifications.get(item.getTaxCode());
                JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = productClassification.getJurisdictionalSalesTaxRules().get(state);
                itemWithRules = item.withJurisdictionalSalesTaxRules(jurisdictionalSalesTaxRules);
                log.debug("Inserting new item with rules : " + itemWithRules);
                itemsWithRules.add(itemWithRules);
            }

            return order.withItems(itemsWithRules);
        });
    }
}
