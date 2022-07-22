package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsCheck;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class NexusOrderAmountExtractor implements NexusDataExtractor<Float, Order> {

    @NonNull
    private ItemsCheck itemsCheck;

    @Override
    public Float extract(@NonNull Order order, @NonNull NexusStateRule nexusStateRule) {
        float amount = 0;

        for(Item item: order.getItems()) {
            if(itemsCheck.isCounted(item,nexusStateRule)){
                amount += item.getTotalPrice();
            }
        }
        return amount;
    }
}
