package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.Getter;
import lombok.NonNull;

@Getter
public class ShippingFeeSalesTaxRatesModifier extends TransactionSalesTaxRatesModifier {

    @NonNull
    protected SalesTaxRatesProvider salesTaxRatesCalculator;

    public ShippingFeeSalesTaxRatesModifier(@NonNull Transaction transaction, @NonNull SalesTaxRate salesTaxRate, SalesTaxRatesProvider salesTaxRatesCalculator) {
        super(transaction, salesTaxRate);
        this.salesTaxRatesCalculator = salesTaxRatesCalculator;
    }

    @Override
    public Transaction modify() {
        ShippingFee modifiedShippingFee = transaction.getShippingFee()
                .withSalesTaxRate(salesTaxRatesCalculator.calculateSalesTaxRate(transaction.getShippingFee().getJurisdictionalSalesTaxRules(), super.getSalesTaxRate()));
        if (nextModifier != null)
            return nextModifier.modify().withShippingFee(modifiedShippingFee);

        return transaction.withShippingFee(modifiedShippingFee);

    }
}
