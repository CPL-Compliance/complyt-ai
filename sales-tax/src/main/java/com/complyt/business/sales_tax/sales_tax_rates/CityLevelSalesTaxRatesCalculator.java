package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CityLevelSalesTaxRatesCalculator implements SalesTaxRatesCalculator<CitySalesTaxRules> {
    /**
     * Calculating sales tax rate regarding the rules of the given item
     * 2 patterns are available :
     * - not taxable
     * - taxable with no special treatment - taking the original sales tax rate
     *
     * @param citySalesTaxRules    - Rules to declare how sales tax rate should be calculated
     * @param originalSalesTaxRate - Sales tax rate given by external resource regarding the current transaction's address
     * @return
     */
    public SalesTaxRates calculate(@NonNull CitySalesTaxRules citySalesTaxRules, @NonNull SalesTaxRates originalSalesTaxRate) {
        if (!citySalesTaxRules.isTaxable()) {
            log.debug("None taxable rule for city - returning 0 City rate");
            SalesTaxRates zeroCitySalesTaxRate = originalSalesTaxRate.withCityRate(0);

            return zeroCitySalesTaxRate;
        }

        if (!citySalesTaxRules.isSpecialTreatment()) {
            SalesTaxRates calculatedRates = modifyRates(originalSalesTaxRate);
            log.debug("None special treatment for city rule - returning original sales tax rate");

            return calculatedRates;
        }

        if (citySalesTaxRules.getCalculationType().equals(CalculationType.FIXED)) {
            SalesTaxRates modifiedRateByFixedTreatment = modifyRatesByFixedTreatment(citySalesTaxRules.getCalculationValue(), originalSalesTaxRate);
            return modifiedRateByFixedTreatment;
        }

        // This means there is a special treatment which is calculation by percentage
        SalesTaxRates modifiedRateByPercentageTreatment = modifyRatesByPercentageTreatment(citySalesTaxRules.getCalculationValue(), originalSalesTaxRate);
        return modifiedRateByPercentageTreatment;
    }

    private SalesTaxRates modifyRatesByFixedTreatment(float cityLevelRate, SalesTaxRates originalSalesTaxRate) {
        SalesTaxRates calculatedRates = modifyRates(originalSalesTaxRate.withCityRate(cityLevelRate));
        log.debug("City sales tax rate after fixed modification: " + cityLevelRate);

        return calculatedRates;
    }

    private SalesTaxRates modifyRatesByPercentageTreatment(float percentageToCut, SalesTaxRates originalSalesTaxRate) {
        double newCityTaxRate = originalSalesTaxRate.cityRate() * percentageToCut;
        SalesTaxRates calculatedRates = modifyRates(originalSalesTaxRate.withCityRate(newCityTaxRate));

        log.debug("State Sales tax rate after percentage modification: " + calculatedRates);

        return calculatedRates;
    }

    private SalesTaxRates modifyRates(SalesTaxRates salesTaxRates) {
        double taxRate = salesTaxRates.cityRate() + salesTaxRates.combinedDistrictRate() +
                salesTaxRates.stateRate() + salesTaxRates.countyRate();

        return new SalesTaxRates(
                salesTaxRates.cityRate(),
                salesTaxRates.countyRate(),
                salesTaxRates.stateRate(),
                taxRate,
                salesTaxRates.combinedDistrictRate(),
                salesTaxRates.ratesMetaData());
    }
}
