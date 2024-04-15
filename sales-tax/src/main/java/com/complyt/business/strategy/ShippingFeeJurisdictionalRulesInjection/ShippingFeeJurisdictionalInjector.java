package com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public interface ShippingFeeJurisdictionalInjector {
    Function<Map<String, ProductClassification>, Transaction> inject(Transaction transaction);
}
