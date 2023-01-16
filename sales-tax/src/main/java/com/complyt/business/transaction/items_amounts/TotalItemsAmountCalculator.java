package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Taxable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TotalItemsAmountCalculator implements AmountCalculator<List<Taxable>> {
    @Override
    public float calculate(@NonNull List<Taxable> items) {
        float amount = 0;
        for (Taxable item : items) {
            amount += item.getTotalPrice();
        }
        log.debug("Total Items price calculated : " + amount);

        return amount;
    }
}
