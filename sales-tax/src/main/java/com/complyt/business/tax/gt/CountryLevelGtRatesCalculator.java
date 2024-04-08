package com.complyt.business.tax.gt;

import com.complyt.domain.transaction.tax.GtRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class CountryLevelGtRatesCalculator implements GtTaxRatesCalculator<JurisdictionalTaxRules> {

    /**
     * Calculating sales tax rate regarding the rules of the given item
     * 4 patterns are available :
     * - not taxable
     * - taxable with no special treatment - taking the original sales tax rate
     * - calculated by fixed value to override the original sales tax rate
     * - calculated by certain percentage of the original sales tax rate
     *
     * @param jurisdictionalTaxRules - Rules to declare how sales tax rate should be calculated
     * @param originalGtRate         - Gt tax rate given by external resource regarding the current transaction's address
     * @return
     */ //todo: fix
    @Override //todo: this code is the same as state and city, they can be the same generic code
    public GtRates calculate(@NonNull JurisdictionalTaxRules jurisdictionalTaxRules, @NonNull GtRates originalGtRate) {
        if (!jurisdictionalTaxRules.isTaxable()) {
            log.debug("None taxable rule - returning sales tax rate that is set to 0");
            GtRates zeroGtRates = GtRates.zeroGtRates();

            return zeroGtRates;
        }

        if (!jurisdictionalTaxRules.isSpecialTreatment()) {
            log.debug("None special treatment for rule - returning original sales tax rate");

            return originalGtRate;
        }

        if (jurisdictionalTaxRules.getCalculationType() == CalculationType.FIXED) {
            log.debug("Returning fixed sales tax rate of: " + jurisdictionalTaxRules.getCalculationValue());
            GtRates modifiedRateByFixedTreatment = modifyRateByFixedTreatment(jurisdictionalTaxRules.getCalculationValue(), originalGtRate);

            return modifiedRateByFixedTreatment;
        }

        log.debug("Returning sales tax rate by percentage cut of: " + jurisdictionalTaxRules.getCalculationValue());
        GtRates modifiedRateByPercentageTreatment = modifyRateByPercentageTreatment(jurisdictionalTaxRules.getCalculationValue(), originalGtRate);

        return modifiedRateByPercentageTreatment;
    }

    private GtRates modifyRateByFixedTreatment(BigDecimal jurisdictionalRuleCountryRate, GtRates gstRates) {
        BigDecimal newTaxRate = gstRates.taxRate().subtract(gstRates.countryRate()).add(jurisdictionalRuleCountryRate);
        GtRates calculatedRate = gstRates.withCountryRate(jurisdictionalRuleCountryRate).withTaxRate(newTaxRate);
        log.debug("State sales tax rate after fixed modification: " + calculatedRate);

        return calculatedRate;
    }

    private GtRates modifyRateByPercentageTreatment(BigDecimal percentageToCut, GtRates gstRates) {
        BigDecimal newTaxRate = gstRates.taxRate().multiply(percentageToCut);
        GtRates calculatedRate = gstRates.withTaxRate(newTaxRate);
        log.debug("State Sales tax rate after percentage modification: " + calculatedRate);

        return calculatedRate;
    }
}
