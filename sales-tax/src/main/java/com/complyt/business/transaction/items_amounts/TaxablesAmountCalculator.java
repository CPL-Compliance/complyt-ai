package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.enums.TaxableCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TaxablesAmountCalculator implements AmountCalculator<Transaction> {

    @Override
    public float calculate(@NonNull Transaction transaction) {
        float amount = 0;
        for (Item item : transaction.getItems()) {
            amount += item.getTaxableCategory() == TaxableCategory.TAXABLE ?
                    item.getTotalPrice() : 0;
        }
        log.debug("Total Taxable items price calculated : " + amount);

        return amount;
    }
}
