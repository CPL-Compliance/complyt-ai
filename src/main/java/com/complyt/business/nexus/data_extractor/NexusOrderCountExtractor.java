package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemStateThresholdQualifier;
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
    private ItemStateThresholdQualifier itemStateThresholdQualifier;

    @Override
    public Integer extract(@NonNull Order order, @NonNull NexusStateRule nexusStateRule) {
        final int COUNTED = 1, NOT_COUNTED = 0;
        boolean itemsQualify = itemStateThresholdQualifier.check(new Pair(order.getItems(), nexusStateRule));

        return itemsQualify ? COUNTED : NOT_COUNTED;
    }
}
