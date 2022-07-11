package com.complyt.domain.nexus;

import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.checker.ItemsCheck;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
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
