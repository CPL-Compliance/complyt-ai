package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesTaxRatesProvider {
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
    public SalesTaxRate calculateSalesTaxRate(@NonNull JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, @NonNull SalesTaxRate originalSalesTaxRate) {
        if (!jurisdictionalSalesTaxRules.isTaxable()) {
            log.info("None taxable rule - returning sales tax rate that is set to 0");

            return SalesTaxRate.zeroSalesTaxRate();
        }

        if (!jurisdictionalSalesTaxRules.isSpecialTreatment()) {
            log.info("None special treatment for rule - returning original sales tax rate");
            return originalSalesTaxRate;
        }

        if (jurisdictionalSalesTaxRules.getCalculationType() == CalculationType.FIXED) {
            log.info("Returning fixed sales tax rate of : " + jurisdictionalSalesTaxRules.getCalculationValue());
            return modifyRateByFixedTreatment(jurisdictionalSalesTaxRules.getCalculationValue(), originalSalesTaxRate);
        }

        log.info("Returning sales tax rate by percentage cut of : " + jurisdictionalSalesTaxRules.getCalculationValue());
        return modifyRateByPercentageTreatment(jurisdictionalSalesTaxRules.getCalculationValue(), originalSalesTaxRate);
    }

    private SalesTaxRate modifyRateByFixedTreatment(float jurisdictionalRuleStateRate, SalesTaxRate salesTaxRate) {
        float newTaxRate = salesTaxRate.getTaxRate() - salesTaxRate.getStateRate() + jurisdictionalRuleStateRate;
        SalesTaxRate calculatedRate = salesTaxRate.withStateRate(jurisdictionalRuleStateRate).withTaxRate(newTaxRate);
        log.info("Sales tax rate after fixed modification : " + calculatedRate);
        return calculatedRate;
    }

    private SalesTaxRate modifyRateByPercentageTreatment(float percentageToCut, SalesTaxRate salesTaxRate) {
        float newTaxRate = salesTaxRate.getTaxRate() * percentageToCut;
        SalesTaxRate calculatedRate = salesTaxRate.withTaxRate(newTaxRate);
        log.info("Sales tax rate after percentage modification : " + calculatedRate);
        return calculatedRate;
    }

}