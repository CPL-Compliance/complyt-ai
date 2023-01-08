package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TotalItemsAmountCalculator implements AmountCalculator<Transaction> {
    @Override
    public float calculate(@NonNull Transaction transaction) {
        float amount = 0;
        for(Item item: transaction.getItems()) {
            amount += item.getTotalPrice();
        }
        log.debug("Total Items price calculated : " + amount);

        return amount;
    }
}
