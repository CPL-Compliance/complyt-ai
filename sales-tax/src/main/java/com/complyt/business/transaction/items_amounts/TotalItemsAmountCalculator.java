package com.complyt.business.transaction.items_amounts;

import com.complyt.business.transaction.BigDecimalProcessor;
import com.complyt.domain.Taxable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class TotalItemsAmountCalculator implements AmountCalculator<List<Taxable>> {

    @Override
    public BigDecimal calculate(@NonNull List<Taxable> items, Boolean isTaxInclusive) {
        BigDecimal amount = BigDecimal.ZERO;
        for (Taxable item : items) {
            amount = isTaxInclusive ? amount.add(item.removeInclusiveSalesTax()) : amount.add(item.getCalculatedTotal());
        }
        log.debug("Total Items price calculated: " + amount);

        return BigDecimalProcessor.removeTrailingZeros(amount);
    }
}
