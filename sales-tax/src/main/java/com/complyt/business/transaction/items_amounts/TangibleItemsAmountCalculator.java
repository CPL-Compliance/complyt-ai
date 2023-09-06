package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
@Slf4j
public class TangibleItemsAmountCalculator implements AmountCalculator<List<Taxable>> {

    @Override
    public BigDecimal calculate(@NonNull List<Taxable> items) {
        BigDecimal amount = BigDecimal.ZERO;
        for (Taxable item : items) {
            amount = item.getTangibleCategory() == TangibleCategory.TANGIBLE ?
                    amount.add(item.getTotalPrice()) : amount;
        }
        log.debug("Total Tangible items price calculated: " + amount);

        return amount;
    }
}
