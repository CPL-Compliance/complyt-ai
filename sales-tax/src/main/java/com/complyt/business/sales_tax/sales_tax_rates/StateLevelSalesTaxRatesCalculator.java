package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StateLevelSalesTaxRatesCalculator implements SalesTaxRatesCalculator<JurisdictionalSalesTaxRules> {
    /**
     * Calculating sales tax rate regarding the rules of the given item
     * 4 patterns are available :
     * - not taxable
     * - taxable with no special treatment - taking the original sales tax rate
     * - calculated by fixed value to override the original sales tax rate
     * - calculated by certain percentage of the original sales tax rate
     *
     * @param jurisdictionalSalesTaxRules   - Rules to declare how sales tax rate should be calculated
     * @param originalSalesTaxRate - Sales tax rate given by external resource regarding the current transaction's address
     * @return
     */
    public SalesTaxRate calculate(@NonNull JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, @NonNull SalesTaxRate originalSalesTaxRate) {
        if (!jurisdictionalSalesTaxRules.taxable()) {
            log.info("None taxable rule - returning sales tax rate that is set to 0");
            SalesTaxRate zeroSalesTaxRate = SalesTaxRate.zeroSalesTaxRate();

            return zeroSalesTaxRate;
        }

        if (!jurisdictionalSalesTaxRules.specialTreatment()) {
            log.info("None special treatment for rule - returning original sales tax rate");

            return originalSalesTaxRate;
        }

        if (jurisdictionalSalesTaxRules.calculationType() == CalculationType.FIXED) {
            log.info("Returning fixed sales tax rate of: " + jurisdictionalSalesTaxRules.calculationValue());
            SalesTaxRate modifiedRateByFixedTreatment = modifyRateByFixedTreatment(jurisdictionalSalesTaxRules.calculationValue(), originalSalesTaxRate);

            return modifiedRateByFixedTreatment;
        }

        log.info("Returning sales tax rate by percentage cut of: " + jurisdictionalSalesTaxRules.calculationValue());
        SalesTaxRate modifiedRateByPercentageTreatment = modifyRateByPercentageTreatment(jurisdictionalSalesTaxRules.calculationValue(), originalSalesTaxRate);

        return modifiedRateByPercentageTreatment;
    }

    private SalesTaxRate modifyRateByFixedTreatment(float jurisdictionalRuleStateRate, SalesTaxRate salesTaxRate) {
        float newTaxRate = salesTaxRate.getTaxRate() - salesTaxRate.getStateRate() + jurisdictionalRuleStateRate;
        SalesTaxRate calculatedRate = salesTaxRate.withStateRate(jurisdictionalRuleStateRate).withTaxRate(newTaxRate);
        log.info("State sales tax rate after fixed modification: " + calculatedRate);

        return calculatedRate;
    }

    private SalesTaxRate modifyRateByPercentageTreatment(float percentageToCut, SalesTaxRate salesTaxRate) {
        float newTaxRate = salesTaxRate.getTaxRate() * percentageToCut;
        SalesTaxRate calculatedRate = salesTaxRate.withTaxRate(newTaxRate);
        log.info("State Sales tax rate after percentage modification: " + calculatedRate);

        return calculatedRate;
    }

}