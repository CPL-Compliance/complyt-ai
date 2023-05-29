package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRates;
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
        float taxRate = originalSalesTaxRate.cityRate() + originalSalesTaxRate.cityDistrictRate() + originalSalesTaxRate.countyDistrictRate()
                + originalSalesTaxRate.countyRate() + originalSalesTaxRate.stateRate();
        log.debug("None special treatment for city rule - returning original sales tax rate");

        return originalSalesTaxRate.withTaxRate(taxRate);
    }

}
