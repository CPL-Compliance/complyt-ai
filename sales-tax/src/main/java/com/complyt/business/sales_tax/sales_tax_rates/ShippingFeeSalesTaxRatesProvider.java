package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingFee;
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

    public ShippingFee setSalesTaxRates(ShippingFee shippingFee, SalesTaxRates salesTaxRates, Address address) {
        SalesTaxRates shippingFeeSalesTaxRate = salesTaxRatesProvider.provide(shippingFee.getJurisdictionalSalesTaxRules(), salesTaxRates, address);
        return shippingFee.withSalesTaxRates(shippingFeeSalesTaxRate);
    }
}
