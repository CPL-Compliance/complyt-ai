package com.complyt.business.transaction.items_amounts;

import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TaxableCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Component
public class TaxableItemsAmountCalculator implements AmountCalculator<List<Taxable>> {

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