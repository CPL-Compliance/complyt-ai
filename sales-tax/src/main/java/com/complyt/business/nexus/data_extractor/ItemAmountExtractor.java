package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.ItemQualificationCheck;
import com.complyt.domain.Item;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import java.util.List;

@AllArgsConstructor
@Component
public class ItemAmountExtractor implements IAmountExtractor {

    @NonNull
    private ItemQualificationCheck itemQualificationCheck;

    @NonNull
    private List<Item> items;

    @NonNull
    private NexusStateRule nexusStateRule;

    public float extract() {
        float amount = 0;
        for (Item item : items) {
            System.out.println("here1:  " + item);
            if (itemQualificationCheck.isQualified(item, nexusStateRule)) {
                amount += item.getTotalPrice();
            }
        }
        return amount;
    }
}
