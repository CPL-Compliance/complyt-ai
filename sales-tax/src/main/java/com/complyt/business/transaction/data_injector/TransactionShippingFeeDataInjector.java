package com.complyt.business.transaction.data_injector;

import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;


@AllArgsConstructor
@Getter
@Slf4j
public abstract class TransactionShippingFeeDataInjector implements TransactionDataInjector<Map<String, ProductClassification>> {

    @NonNull
    protected final Transaction transaction;

    public boolean shouldInject(Map<String, ProductClassification> mapTaxCodesToClassifications) {
        if (transaction.getShippingFee() == null) {
            log.debug("Transaction doesn't have shipping fee");
            return false;
        }
        if (!mapTaxCodesToClassifications.containsKey(transaction.getShippingFee().getTaxCode())) {
            log.debug("Shipping fee's tax code does not exist in given classifications list - not injecting jurisdictional rules to it");
            return false;
        }

        return true;
    }
}
