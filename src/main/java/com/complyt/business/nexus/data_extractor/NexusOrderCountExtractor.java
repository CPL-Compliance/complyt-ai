package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsCheck;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusStateRule;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class NexusOrderCountExtractor implements NexusDataExtractor<Integer, Order> {

    @NonNull
    private ItemsCheck itemsCheck;

    @Override
    public Integer extract(@NonNull Order order, @NonNull NexusStateRule nexusStateRule) {
        final int COUNTED = 1, NOT_COUNTED = 0;

        boolean customerTypeExists = nexusStateRule.getCustomerTypes().contains(order.getCustomer().getCustomerType());
        boolean itemsQualify = itemsCheck.check(new Pair(order.getItems(),nexusStateRule));

        if(itemsQualify && customerTypeExists) {
            return COUNTED;
        }

        return NOT_COUNTED;
    }
}
