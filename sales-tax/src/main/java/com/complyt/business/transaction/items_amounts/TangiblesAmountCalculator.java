package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TangibleCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TangiblesAmountCalculator implements AmountCalculator<Transaction> {

    @Override
    public float calculate(@NonNull Transaction transaction) {
        float amount = 0;
        for (Item item : transaction.getItems()) {
            amount += item.getTangibleCategory() == TangibleCategory.TANGIBLE ?
                    item.getTotalPrice() : 0;
        }
        log.debug("Total Tangible items price calculated : " + amount);

        return amount;
    }
}
