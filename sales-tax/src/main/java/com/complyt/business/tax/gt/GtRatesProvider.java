package com.complyt.business.tax.gt;

import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
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
public class GtRatesProvider {
    @NonNull
    GtTaxRatesCalculator<JurisdictionalTaxRules> countryLevelGtRatesCalculator;
    GtTaxRatesCalculator<SubJurisdictionalTaxRules> regionLevelGtRatesCalculator;

    /**
     * Calculating gt rates for country and region level if exists.
     * We must make sure that countries are always being calculated before region rate
     * so there won't be a tax rate override
     */
    public GtRates provide(@NonNull JurisdictionalTaxRules jurisdictionalTaxRules, @NonNull GtRates originalGtRates, @NonNull GtAddress gtAddress) {
        GtRates calculatedRates = countryLevelGtRatesCalculator.calculate(jurisdictionalTaxRules, originalGtRates);

        if (jurisdictionalTaxRules.getRegions() != null && jurisdictionalTaxRules.getRegions().containsKey(gtAddress.region())) {
            SubJurisdictionalTaxRules regionTaxRules = jurisdictionalTaxRules.getRegions().get(gtAddress.region());
            calculatedRates = regionLevelGtRatesCalculator.calculate(regionTaxRules, calculatedRates.withRegionRate(originalGtRates.regionRate()));
        }

        log.debug("Rates returned after calculation: " + calculatedRates);

        return calculatedRates;
    }
}