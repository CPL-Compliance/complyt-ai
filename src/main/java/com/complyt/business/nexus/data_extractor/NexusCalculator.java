package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.CustomerType;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
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
        log.debug("Calculating amount and count for all orders on timeframe : " + nexusStateRule.getTimeFrame());

        int count = 0;
        float amount = 0;
        for (Order order : orders) {
            CustomerType customerType = order.getCustomer().getCustomerType();
            boolean customerTypeExists = nexusStateRule.getCustomerTypes().contains(customerType);
            if(!customerTypeExists){
                log.debug("Customer of type "  + customerType + " does not exist in state rule customer types, order does not count in calculation");
                continue;
            }
            count += nexusOrderCountExtractor.extract(order, nexusStateRule);
            amount += nexusOrderAmountExtractor.extract(order, nexusStateRule);
        }

        log.debug("Calculated total amount of : " + amount + ", and count : " + count);
        return new NexusCalculationSummary(count,amount);
    }
}
