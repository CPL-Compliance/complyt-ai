package com.complyt.business.transaction.items_amounts;

import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class TangibleItemsAmountCalculator implements AmountCalculator<List<Taxable>> {

    @Override
    public float calculate(@NonNull List<Taxable> items) {
        float amount = 0;
        for (Taxable item : items) {
            amount += item.getTangibleCategory() == TangibleCategory.TANGIBLE ?
                    item.getTotalPrice() : 0;
        }
        log.debug("Total Tangible items price calculated : " + amount);

        return amount;
    }
}
