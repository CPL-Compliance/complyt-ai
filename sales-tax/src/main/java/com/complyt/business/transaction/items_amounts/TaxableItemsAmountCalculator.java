package com.complyt.business.transaction.items_amounts;

import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalTaxRules;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@AllArgsConstructor
public class TaxableItemsAmountCalculator implements AmountCalculator<List<Taxable>> {

    String shippingAddressCity;

    String shippingAddressRegion;

    @Override
    public BigDecimal calculate(@NonNull List<Taxable> items, Boolean isTaxInclusive) {
        BigDecimal amount = BigDecimal.ZERO;
        for (Taxable item : items) {
            amount = shouldAddToSum(item) ?
                    isTaxInclusive ? amount.add(item.removeInclusiveSalesTax()) : amount.add(item.getCalculatedTotal()) :
                    amount;
        }
        log.debug("Total Taxable items price calculated: " + amount);

        return BigDecimalProcessor.removeTrailingZeros(amount);
    }

    private boolean shouldAddToSum(Taxable taxable) {
        // Check jurisdictional sales tax rules
        JurisdictionalSalesTaxRules salesTaxRules = taxable.getJurisdictionalSalesTaxRules();
        if (isTaxableBySalesTaxRules(salesTaxRules)) {
            return true;
        }

        // Check jurisdictional tax rules
        JurisdictionalTaxRules taxRules = taxable.getJurisdictionalTaxRules();
        return isTaxableByTaxRules(taxRules);
    }

    private boolean isTaxableBySalesTaxRules(JurisdictionalSalesTaxRules rules) {
        if (rules == null) {
            return false;
        }
        if (rules.isTaxable()) {
            return true;
        }
        return rules.getCities() != null
                && rules.getCities().get(shippingAddressCity).isTaxable();
    }

    private boolean isTaxableByTaxRules(JurisdictionalTaxRules rules) {
        if (rules == null) {
            return false;
        }
        if (rules.isTaxable()) {
            return true;
        }
        return rules.getRegions() != null
                && rules.getRegions().get(shippingAddressRegion).isTaxable();
    }

}