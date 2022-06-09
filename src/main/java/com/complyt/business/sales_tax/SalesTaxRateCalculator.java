package com.complyt.business.sales_tax;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesTaxRateCalculator {
    /** Calculating sales tax rate regarding the rules of the given item
     * 4 patterns are available :
     * - not taxable
     * - taxable with no special treatment - taking the original sales tax rate
     * - calculated by fixed value to override the original sales tax rate
     * - calculated by certain percentage of the original sales tax rate
     * @param jurisdictionalSalesTaxRules - Rules to declare how sales tax rate should be calculated
     * @param originalSalesTaxRate - Sales tax rate given by external resource for the current order's address
     * @return
     */
    public SalesTaxRate calculateSalesTaxRate(JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, SalesTaxRate originalSalesTaxRate) {
        if (!jurisdictionalSalesTaxRules.isTaxable()) {
            log.info("None taxable rule - setting sales tax rate to 0");
            return new SalesTaxRate(0, 0, 0, 0, 0, 0);
        }

        if (!jurisdictionalSalesTaxRules.isSpecialTreatment()) {
            log.info("None special treatment for rule - setting sales tax rate to original rate");
            return originalSalesTaxRate;
        }

        if (jurisdictionalSalesTaxRules.getCalculationType() == CalculationType.FIXED) {
            log.info("setting sales tax rate to fixed rate with calculation value of " + jurisdictionalSalesTaxRules.getCalculationValue());
            return modifyRateByFixedTreatment(jurisdictionalSalesTaxRules.getCalculationValue(), originalSalesTaxRate);
        }
        log.info("setting sales tax rate to percentage rate with calculation value of " + jurisdictionalSalesTaxRules.getCalculationValue());
        return modifyRateByPercentageTreatment(jurisdictionalSalesTaxRules.getCalculationValue(), originalSalesTaxRate);
    }

    private SalesTaxRate modifyRateByFixedTreatment(float jurisdictionalRuleStateRate, SalesTaxRate salesTaxRate) {
        float newTaxRate = salesTaxRate.getTaxRate() - salesTaxRate.getStateRate() + jurisdictionalRuleStateRate;
        SalesTaxRate calculatedRate = salesTaxRate.withStateRate(jurisdictionalRuleStateRate).withTaxRate(newTaxRate);
        log.info("Sales tax rate after fixed modification : " + calculatedRate);
        return calculatedRate;
    }

    private SalesTaxRate modifyRateByPercentageTreatment(float jurisdictionalRuleCalculationValue, SalesTaxRate salesTaxRate) {
        float newStateRate = salesTaxRate.getStateRate() * jurisdictionalRuleCalculationValue;
        float newTaxRate = salesTaxRate.getTaxRate() - salesTaxRate.getStateRate() + newStateRate;
        SalesTaxRate calculatedRate = salesTaxRate.withStateRate(newStateRate).withTaxRate(newTaxRate);
        log.info("Sales tax rate after percentage modification : " + calculatedRate);
        return calculatedRate;
    }
}