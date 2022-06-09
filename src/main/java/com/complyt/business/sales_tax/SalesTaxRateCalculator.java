package com.complyt.business.sales_tax;

import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SalesTaxRateCalculator {
    public SalesTaxRate calculateSalesTaxRate(JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, SalesTaxRate originalSalesTaxRate) {
        if (!jurisdictionalSalesTaxRules.isTaxable()) {
            log.debug("None taxable rule - setting sales tax rate to 0");
            return new SalesTaxRate(0, 0, 0, 0, 0, 0);
        }

        if (!jurisdictionalSalesTaxRules.isSpecialTreatment()) {
            log.debug("None special treatment for rule - setting sales tax rate to original rate");
            return originalSalesTaxRate;
        }

        if (jurisdictionalSalesTaxRules.getCalculationType() == CalculationType.FIXED) {
            log.debug("setting sales tax rate to fixed rate with calculation value of " + jurisdictionalSalesTaxRules.getCalculationValue());
            return modifyRateByFixedTreatment(jurisdictionalSalesTaxRules.getCalculationValue(), originalSalesTaxRate);
        }
        log.debug("setting sales tax rate to percentage rate with calculation value of " + jurisdictionalSalesTaxRules.getCalculationValue());
        return modifyRateByPercentageTreatment(jurisdictionalSalesTaxRules.getCalculationValue(), originalSalesTaxRate);
    }

    private SalesTaxRate modifyRateByFixedTreatment(float jurisdictionalRuleStateRate, SalesTaxRate salesTaxRate) {
        float newTaxRate = salesTaxRate.getTaxRate() - salesTaxRate.getStateRate() + jurisdictionalRuleStateRate;
        SalesTaxRate calculatedRate = salesTaxRate.withStateRate(jurisdictionalRuleStateRate).withTaxRate(newTaxRate);
        log.debug("Sales tax rate after fixed modification : " + calculatedRate);
        return calculatedRate;
    }

    private SalesTaxRate modifyRateByPercentageTreatment(float jurisdictionalRuleCalculationValue, SalesTaxRate salesTaxRate) {
        float newStateRate = salesTaxRate.getStateRate() * jurisdictionalRuleCalculationValue;
        float newTaxRate = salesTaxRate.getTaxRate() - salesTaxRate.getStateRate() + newStateRate;
        SalesTaxRate calculatedRate = salesTaxRate.withStateRate(newStateRate).withTaxRate(newTaxRate);
        log.debug("Sales tax rate after percentage modification : " + calculatedRate);
        return calculatedRate;
    }
}