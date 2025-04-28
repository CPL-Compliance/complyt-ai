package com.complyt.business.tax.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.RatesMetaData;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class StateLevelSalesTaxRatesCalculator implements TaxRatesCalculator<JurisdictionalSalesTaxRules> {
    /**
     * Calculating sales tax rate regarding the rules of the given item
     * 4 patterns are available :
     * - not taxable
     * - taxable with no special treatment - taking the original sales tax rate
     * - calculated by fixed value to override the original sales tax rate
     * - calculated by certain percentage of the original sales tax rate
     *
     * @param jurisdictionalSalesTaxRules - Rules to declare how sales tax rate should be calculated
     * @param originalSalesTaxRate        - Sales tax rate given by external resource regarding the current transaction's address
     * @return
     */
    public SalesTaxRates calculate(@NonNull JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, @NonNull SalesTaxRates originalSalesTaxRate) {
        if (!jurisdictionalSalesTaxRules.isTaxable()) {
            log.debug("None taxable rule - returning sales tax rate that is set to 0");
            SalesTaxRates zeroSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();

            return zeroSalesTaxRate;
        }

        if (!jurisdictionalSalesTaxRules.isSpecialTreatment()) {
            log.debug("None special treatment for rule - returning original sales tax rate");

            return originalSalesTaxRate;
        }

        if (jurisdictionalSalesTaxRules.getCalculationType() == CalculationType.FIXED) {
            log.debug("Returning fixed sales tax rate of: " + jurisdictionalSalesTaxRules.getCalculationValue());
            SalesTaxRates modifiedRateByFixedTreatment = modifyRateByFixedTreatment(jurisdictionalSalesTaxRules.getCalculationValue());

            return modifiedRateByFixedTreatment;
        }

        log.debug("Returning sales tax rate by percentage cut of: " + jurisdictionalSalesTaxRules.getCalculationValue());
        SalesTaxRates modifiedRateByPercentageTreatment = modifyRateByPercentageTreatment(jurisdictionalSalesTaxRules.getCalculationValue(), originalSalesTaxRate);

        return modifiedRateByPercentageTreatment;
    }

    private SalesTaxRates modifyRateByFixedTreatment(BigDecimal jurisdictionalRuleStateRate) {
        SalesTaxRates zeroSalesTaxRate = SalesTaxRates.zeroSalesTaxRate();
        SalesTaxRates calculatedRate = zeroSalesTaxRate.withStateRate(jurisdictionalRuleStateRate).withTaxRate(jurisdictionalRuleStateRate);
        log.debug("State sales tax rate after fixed modification: " + calculatedRate);

        return calculatedRate;
    }

    private SalesTaxRates modifyRateByPercentageTreatment(BigDecimal percentageToCut, SalesTaxRates salesTaxRates) {
        RatesMetaData originalMetaData = salesTaxRates.ratesMetaData();
        RatesMetaData updatedMetaData = null;

        if (originalMetaData != null) {
            updatedMetaData = new RatesMetaData(
                    multiplyIfPresent(originalMetaData.cityDistrictRate(), percentageToCut),
                    multiplyIfPresent(originalMetaData.countyDistrictRate(), percentageToCut),
                    multiplyIfPresent(originalMetaData.specialDistrictRate(), percentageToCut)
            );
        }

        return new SalesTaxRates(
                multiplyIfPresent(salesTaxRates.stateRate(), percentageToCut), // state
                multiplyIfPresent(salesTaxRates.countyRate(), percentageToCut), // county
                multiplyIfPresent(salesTaxRates.cityRate(), percentageToCut), // city
                multiplyIfPresent(salesTaxRates.combinedDistrictRate(), percentageToCut), // combined
                updatedMetaData, // Metadata
                multiplyIfPresent(salesTaxRates.mtaRate(), percentageToCut), // mta
                multiplyIfPresent(salesTaxRates.spdRate(), percentageToCut), // spd
                multiplyIfPresent(salesTaxRates.otherRate(), percentageToCut), // other
                multiplyIfPresent(salesTaxRates.taxRate(), percentageToCut) // taxRate
        );
    }

    private BigDecimal multiplyIfPresent(BigDecimal value, BigDecimal multiplier) {
        return value != null ? value.multiply(multiplier).stripTrailingZeros() : null;
    }
}