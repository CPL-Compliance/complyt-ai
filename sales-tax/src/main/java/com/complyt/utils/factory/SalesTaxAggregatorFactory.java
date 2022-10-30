package com.complyt.utils.factory;

import com.complyt.business.sales_tax.checker.TaxableItemExistenceCheck;
import com.complyt.business.sales_tax.sales_tax_amount.ISalesTaxCalculator;
import com.complyt.business.sales_tax.sales_tax_amount.ItemsSalesTaxCalculator;
import com.complyt.business.sales_tax.sales_tax_amount.SalesTaxAggregator;
import com.complyt.business.sales_tax.sales_tax_amount.ShippingFeeSalesTaxCalculator;
import com.complyt.domain.Transaction;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@NonNull
@Component
@AllArgsConstructor
public class SalesTaxAggregatorFactory {

    @NonNull
    private TaxableItemExistenceCheck taxableItemExistenceCheck;

    public SalesTaxAggregator createSalesTaxAggregator(@NonNull Transaction transaction) {
        List<ISalesTaxCalculator> calculators = new ArrayList<>();

        if (transaction.getItems() != null) {
            calculators.add(new ItemsSalesTaxCalculator(transaction.getItems()));
        }

        if (transaction.getShippingFee() != null && taxableItemExistenceCheck.hasTaxableItem(transaction.getItems())) {
            calculators.add(new ShippingFeeSalesTaxCalculator(transaction.getShippingFee()));
        }

        return new SalesTaxAggregator(calculators);
    }
}
