package com.complyt.business.transaction.data_injector;

import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Slf4j
public abstract class TransactionShippingFeeInjectionChecker implements TransactionDataInjector<Map<String, ProductClassification>> {

    public boolean shouldInject(Map<String, ProductClassification> mapTaxCodesToClassifications, Transaction transaction) {
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
