package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class ItemsSalesTaxRatesModifier extends TransactionSalesTaxRatesModifier {

    @NonNull
    protected SalesTaxRatesProvider salesTaxRatesCalculator;

    public ItemsSalesTaxRatesModifier(@NonNull Transaction transaction, @NonNull SalesTaxRate salesTaxRate, SalesTaxRatesProvider salesTaxRatesCalculator) {
        super(transaction, salesTaxRate);
        this.salesTaxRatesCalculator = salesTaxRatesCalculator;
    }

    @Override
    public Transaction modify() {
        List<Item> modifiedItems = transaction.getItems().stream()
                .map(item -> item.withSalesTaxRate(salesTaxRatesCalculator.calculateSalesTaxRate(item.getJurisdictionalSalesTaxRules(), salesTaxRate)))
                .collect(Collectors.toList());
        if (nextModifier != null)
            return nextModifier.modify().withItems(modifiedItems);

        return transaction.withItems(modifiedItems);
    }

}
