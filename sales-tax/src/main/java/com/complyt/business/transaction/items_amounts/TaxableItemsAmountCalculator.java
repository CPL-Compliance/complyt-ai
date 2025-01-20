package com.complyt.business.transaction.items_amounts;

import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TaxableCategory;
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
            amount = item.getTaxableCategory().equals(TaxableCategory.TAXABLE) ?
                    isTaxInclusive ? amount.add(item.removeInclusiveSalesTax()) : amount.add(item.getCalculatedTotal()) :
                    amount;
        }
        log.debug("Total Taxable items price calculated: " + amount);

        return BigDecimalProcessor.removeTrailingZeros(amount);
    }

}