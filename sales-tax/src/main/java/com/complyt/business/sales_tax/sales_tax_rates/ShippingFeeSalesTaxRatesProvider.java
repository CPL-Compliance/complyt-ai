package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.sales_tax.SalesTaxRate;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ShippingFeeSalesTaxRatesProvider implements TaxableSalesTaxRatesProvider<ShippingFee> {

    @NonNull
    private SalesTaxRatesProvider salesTaxRatesProvider;

    public ShippingFee setSalesTaxRates(ShippingFee shippingFee, SalesTaxRate salesTaxRate, Address address) {
        SalesTaxRate shippingFeeSalesTaxRate = salesTaxRatesProvider.provide(shippingFee.getJurisdictionalSalesTaxRules(), salesTaxRate, address);
        return shippingFee.withSalesTaxRate(shippingFeeSalesTaxRate);
    }
}
