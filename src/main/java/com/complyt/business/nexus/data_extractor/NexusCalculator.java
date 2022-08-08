package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.CustomerType;
import com.complyt.domain.Transaction;
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
    private NexusTransactionAmountExtractor nexusTransactionAmountExtractor;

    @NonNull
    private NexusTransactionCountExtractor nexusTransactionCountExtractor;

    public NexusCalculationSummary calculate(List<Transaction> transactions, NexusStateRule nexusStateRule) {
        log.debug("Calculating amount and count for all transactions on timeframe : " + nexusStateRule.getTimeFrame());

        int count = 0;
        float amount = 0;
        for (Transaction transaction : transactions) {
            CustomerType customerType = transaction.getCustomer().getCustomerType();
            boolean customerTypeExists = nexusStateRule.getCustomerTypes().contains(customerType);
            if(!customerTypeExists){
                log.debug("Customer of type "  + customerType + " does not exist in state rule customer types, transaction does not count in calculation");
                continue;
            }
            count += nexusTransactionCountExtractor.extract(transaction, nexusStateRule);
            amount += nexusTransactionAmountExtractor.extract(transaction, nexusStateRule);
        }

        log.debug("Calculated total amount of : " + amount + ", and count : " + count);
        return new NexusCalculationSummary(count,amount);
    }
}
