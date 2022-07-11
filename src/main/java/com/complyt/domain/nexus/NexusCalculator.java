package com.complyt.domain.nexus;

import com.complyt.domain.Order;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class NexusCalculator {
    @NonNull
    private NexusOrderAmountExtractor nexusOrderAmountExtractor;

    @NonNull
    private NexusOrderCountExtractor nexusOrderCountExtractor;

    public NexusCalculationSummary calculate(List<Order> orders, NexusStateRule nexusStateRule) {
        int count = 0;
        float amount = 0;
        for (Order order : orders) {
            count += nexusOrderCountExtractor.extract(order, nexusStateRule);
            amount += nexusOrderAmountExtractor.extract(order, nexusStateRule);
        }
        return new NexusCalculationSummary(count,amount);
    }
}
