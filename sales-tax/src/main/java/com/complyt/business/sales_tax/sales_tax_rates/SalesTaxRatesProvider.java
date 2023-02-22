package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.Address;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.SalesTaxRules;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SalesTaxRatesProvider {

    @NonNull
    SalesTaxRatesCalculator stateLevelSalesTaxRatesCalculator;

    @NonNull
    SalesTaxRatesCalculator cityLevelSalesTaxRatesCalculator;

    /**
     * Calculating sales tax rates for state and city level if exists.
     * We must make sure that State rates are always being calculated before city's rate
     * so there won't be a tax rate override
     */
    public SalesTaxRate provide(@NonNull JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, @NonNull SalesTaxRate originalSalesTaxRate, @NonNull Address address) {
        SalesTaxRate calculatedRates = stateLevelSalesTaxRatesCalculator.calculate(jurisdictionalSalesTaxRules, originalSalesTaxRate);

        if (jurisdictionalSalesTaxRules.getCities() != null && jurisdictionalSalesTaxRules.getCities().containsKey(address.getCity())) {
            SalesTaxRules citySalesTaxRules = jurisdictionalSalesTaxRules.getCities().get(address.getCity());
            calculatedRates = cityLevelSalesTaxRatesCalculator.calculate(citySalesTaxRules, calculatedRates.withCityRate(originalSalesTaxRate.getCityRate()));
        }
        log.debug("Rates returned after calculation: " + calculatedRates);

        return calculatedRates;
    }

}