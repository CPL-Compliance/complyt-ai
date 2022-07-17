package com.complyt.business.order;

import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.enums.TangibleCategory;
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
public class OrderTangibleCategoryInjector implements OrderDataInjector<ProductClassification> {
    @NonNull
    private final Order order;

    @Override
    public Mono<Order> act(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        return Mono.fromCallable(() -> {
            log.info("Setting tangible categories to order's items");
            List<Item> modifiedItems = new ArrayList<>();

            for(Item item : order.getItems()) {
                ProductClassification productClassification = mapTaxCodesToClassifications.get(item.getTaxCode());
                TangibleCategory tangibleCategory = productClassification.getTangibleCategory();
                Item newItem = item.withTangibleCategory(tangibleCategory);
                modifiedItems.add(newItem);
            }

            Order modifiedOrder = order.withItems(modifiedItems);
            log.debug("Order with items with tangible categories injected : " + modifiedOrder);
            return modifiedOrder;
        });
    }
}
