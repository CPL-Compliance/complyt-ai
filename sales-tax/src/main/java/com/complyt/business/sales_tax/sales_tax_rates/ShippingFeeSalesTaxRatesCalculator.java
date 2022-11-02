package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.ShippingFee;
import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ShippingFeeSalesTaxRatesCalculator {

    @NonNull
    private SalesTaxRatesProvider salesTaxRatesProvider;

    public ShippingFee setSalesTaxRates(ShippingFee shippingFee, SalesTaxRate salesTaxRate) {
        SalesTaxRate shippingFeeSalesTaxRate = salesTaxRatesProvider.calculateSalesTaxRate(shippingFee.getJurisdictionalSalesTaxRules(), salesTaxRate);
        return shippingFee.withSalesTaxRate(shippingFeeSalesTaxRate);
    }
}
