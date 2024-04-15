package com.complyt.business.strategy.items_jurisdictional_rules_injection;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ItemsJurisdictionalInjector {
    Function<Map<String, ProductClassification>, List<Item>> inject(Transaction transaction);
}
