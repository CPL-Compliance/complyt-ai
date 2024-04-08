package com.complyt.business.tax.gt;

import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
import com.complyt.domain.transaction.ShippingFee;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@AllArgsConstructor
public class ShippingFeeGtRatesProvider implements TaxableGtRatesProvider<ShippingFee> {

    @NonNull
    private GtRatesProvider gtRatesProvider;

    @Override
    public ShippingFee setGtRates(ShippingFee shippingFee, GtRates gtRates, GtAddress gtAddress) {
        GtRates shippingFeeGtRate = gtRatesProvider.provide(shippingFee.getJurisdictionalTaxRules(), gtRates, gtAddress);
        return shippingFee.withGtRates(shippingFeeGtRate);
    }
}
