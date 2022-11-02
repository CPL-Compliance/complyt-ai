package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationCheck;
import com.complyt.domain.Item;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.List;

@EqualsAndHashCode
@AllArgsConstructor
public class ItemAmountExtractor implements IAmountExtractor {

    @NonNull
    private QualificationCheck qualificationCheck;

    @NonNull
    private List<Item> items;

    @NonNull
    private NexusStateRule nexusStateRule;

    public float extract() {
        float amount = 0;
        for (Item item : items) {
            if (qualificationCheck.isQualified(item, nexusStateRule)) {
                amount += item.getTotalPrice();
            }
        }
        return amount;
    }
}
