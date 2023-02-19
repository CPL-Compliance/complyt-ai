package com.complyt.business.sales_tax.sales_tax_rates;

import com.complyt.domain.sales_tax.SalesTaxRate;
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
    public SalesTaxRate calculate(@NonNull CitySalesTaxRules citySalesTaxRules, @NonNull SalesTaxRate originalSalesTaxRate) {
        if (!citySalesTaxRules.taxable()) {
            log.debug("None taxable rule for city - returning 0 City rate");
            SalesTaxRate zeroCitySalesTaxRate = originalSalesTaxRate.withCityRate(0);

            return zeroCitySalesTaxRate;
        }
        float taxRate = originalSalesTaxRate.getCityRate() + originalSalesTaxRate.getCityDistrictRate() + originalSalesTaxRate.getCountyDistrictRate()
                + originalSalesTaxRate.getCountyRate() + originalSalesTaxRate.getStateRate();
        log.debug("None special treatment for city rule - returning original sales tax rate");

        return originalSalesTaxRate.withTaxRate(taxRate);
    }

}
