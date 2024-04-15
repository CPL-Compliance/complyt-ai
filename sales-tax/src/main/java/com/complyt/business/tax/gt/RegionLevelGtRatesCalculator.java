package com.complyt.business.tax.gt;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.SubJurisdictionalTaxRules;
import com.complyt.domain.transaction.tax.GtRates;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class RegionLevelGtRatesCalculator implements GtTaxRatesCalculator<SubJurisdictionalTaxRules> {

    /**
     * Calculating sales tax rate regarding the rules of the given item
     * 4 patterns are available :
     * - not taxable
     * - taxable with no special treatment - taking the original gt rate
     * - calculated by fixed value to override the original gt rate
     * - calculated by certain percentage of the original gt rate
     *
     * @param regionJurisdictionalTaxRules - Rules to declare how sales tax rate should be calculated
     * @param originalGtRate        - Gt rate given by external resource regarding the current transaction's address
     * @return
     */
    @Override //todo: this code is the same as state and city, they can be the same generic code
    public GtRates calculate(@NonNull SubJurisdictionalTaxRules regionJurisdictionalTaxRules, @NonNull GtRates originalGtRate) {
        if (!regionJurisdictionalTaxRules.isTaxable()) {
            log.debug("None taxable rule for region - returning 0 City rate");
            GtRates zeroRegionGtRates = originalGtRate.withRegionRate(BigDecimal.ZERO);

            return zeroRegionGtRates;
        }

        if (!regionJurisdictionalTaxRules.isSpecialTreatment()) {
            GtRates calculatedGtRates = modifyRates(originalGtRate);
            log.debug("None special treatment for region rule - returning original gt rate");

            return calculatedGtRates;
        }

        if (regionJurisdictionalTaxRules.getCalculationType() == CalculationType.FIXED) {
            log.debug("Returning fixed sales tax rate of: " + regionJurisdictionalTaxRules.getCalculationValue());
            GtRates modifiedRateByFixedTreatment = modifyRateByFixedTreatment(regionJurisdictionalTaxRules.getCalculationValue(), originalGtRate);

            return modifiedRateByFixedTreatment;
        }

        log.debug("Returning sales tax rate by percentage cut of: " + regionJurisdictionalTaxRules.getCalculationValue());
        GtRates modifiedRateByPercentageTreatment = modifyRateByPercentageTreatment(regionJurisdictionalTaxRules.getCalculationValue(), originalGtRate);

        return modifiedRateByPercentageTreatment;
    }

    private GtRates modifyRateByFixedTreatment(BigDecimal jurisdictionalRuleRegionRate, GtRates originalGtRates) {
        GtRates calculatedRates = modifyRates(originalGtRates.withRegionRate(jurisdictionalRuleRegionRate));

        log.debug("Region gt rate after fixed modification: " + calculatedRates);

        return calculatedRates;
    }

    private GtRates modifyRateByPercentageTreatment(BigDecimal percentageToCut, GtRates originalGtRates) {
        BigDecimal newRegionTaxRate = originalGtRates.regionRate().multiply(percentageToCut);
        GtRates calculatedRate = modifyRates(originalGtRates.withRegionRate(newRegionTaxRate));
        log.debug("State Sales tax rate after percentage modification: " + calculatedRate);

        return calculatedRate;
    }

    private GtRates modifyRates(GtRates gtRates) {
        BigDecimal taxRate = gtRates.countryRate().add(gtRates.regionRate());

        return new GtRates(
               gtRates.countryRate(),
                gtRates.regionRate(),
                taxRate);
    }
}
