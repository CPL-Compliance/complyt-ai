package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemStateThresholdQualifier;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

import javax.swing.text.html.Option;

@Component
@AllArgsConstructor
public class NexusOrderAmountExtractor implements NexusDataExtractor<Float, Order> {

    @NonNull
    private ItemStateThresholdQualifier itemStateThresholdQualifier;

    @Override
    public Float extract(@NonNull Order order, @NonNull NexusStateRule nexusStateRule) {
        float amount = 0;
        for(Item item: order.getItems()) {
            if(itemStateThresholdQualifier.isCounted(item,nexusStateRule)){
                amount += item.getTotalPrice();
            }
        }
        return amount;
    }
}
