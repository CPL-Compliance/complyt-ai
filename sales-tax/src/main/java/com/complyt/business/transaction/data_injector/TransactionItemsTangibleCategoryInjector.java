package com.complyt.business.transaction.data_injector;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.observability.ContextLogger;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@EqualsAndHashCode
@Getter
@Component
@Slf4j
public class TransactionItemsTangibleCategoryInjector implements TransactionDataInjector<Map<String, ProductClassification>> {

    @Override
    public Mono<Transaction> inject(Map<String, ProductClassification> mapTaxCodesToClassifications, @NonNull Transaction transaction) {
        return ContextLogger.observeCtx("Setting tangible categories to Transaction's items", log::debug)
                .then(Mono.just(transaction.withItems(createItemsWithTangibleCategories(mapTaxCodesToClassifications, transaction))))
                .flatMap(modifiedTransaction -> ContextLogger.observeCtx("Transaction with items with tangible categories injected : " + modifiedTransaction, log::debug)
                        .thenReturn(modifiedTransaction));
    }

    private List<Item> createItemsWithTangibleCategories(Map<String, ProductClassification> mapTaxCodesToClassifications, Transaction transaction) {
        List<Item> modifiedItems = new ArrayList<>();

        for (Item item : transaction.getItems()) {
            ProductClassification productClassification = mapTaxCodesToClassifications.get(item.getTaxCode());
            TangibleCategory category = productClassification.getTangibleCategory();
            Item newItem = item.withTangibleCategory(category);

            log.debug("Inserting new item with tangible category : " + category);
            modifiedItems.add(newItem);
        }

        return modifiedItems;
    }
}
