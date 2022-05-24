package com.complyt.business.tax_reliefs;

import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.springframework.stereotype.Component;

@Component
public class JurisdictionalSalesTaxController {
    public SalesTaxRate getRateByRules(JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules, SalesTaxRate salesTaxRateFromService, Item item) {
        if (!jurisdictionalSalesTaxRules.isTaxable()) {
            return new SalesTaxRate(0, 0, 0, 0, 0, 0);
        }

        if (!jurisdictionalSalesTaxRules.isSpecialTreatment()) {
            return salesTaxRateFromService;
        }

        if (jurisdictionalSalesTaxRules.getCalculationType() == CalculationType.FIXED) {
            return getFixedRate(jurisdictionalSalesTaxRules.getCalculationValue(),salesTaxRateFromService);
        }

        return getRateByPercentage(jurisdictionalSalesTaxRules.getCalculationValue(), salesTaxRateFromService);
    }

    private SalesTaxRate getFixedRate(float jurisdictionalRuleStateRate, SalesTaxRate salesTaxRateFromService) {
        float newTaxRate = salesTaxRateFromService.getTaxRate() - salesTaxRateFromService.getStateRate() + jurisdictionalRuleStateRate;

        return salesTaxRateFromService.withStateRate(jurisdictionalRuleStateRate).withTaxRate(newTaxRate);
    }

    private SalesTaxRate getRateByPercentage(float jurisdictionalRuleCalculationValue, SalesTaxRate salesTaxRateFromService) {
        float newStateRate = salesTaxRateFromService.getStateRate() * jurisdictionalRuleCalculationValue;
        float newTaxRate = salesTaxRateFromService.getTaxRate() - salesTaxRateFromService.getStateRate() + newStateRate;

        return salesTaxRateFromService.withStateRate(newStateRate).withTaxRate(newTaxRate);
    }
}