package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemStateThresholdQualifier;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NexusTransactionAmountExtractor implements NexusDataExtractor<Float, Transaction> {

    @NonNull
    private ItemStateThresholdQualifier itemStateThresholdQualifier;

    @Override
    public Float extract(@NonNull Transaction transaction, @NonNull NexusStateRule nexusStateRule) {
        float amount = 0;
        for(Item item: transaction.getItems()) {
            if(itemStateThresholdQualifier.isCounted(item,nexusStateRule)){
                amount += item.getTotalPrice();
            }
        }
        return amount;
    }
}
