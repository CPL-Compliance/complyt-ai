package com.complyt.business.nexus.data_extractor;

import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
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
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @NonNull
    private NexusTransactionCountExtractor nexusTransactionCountExtractor;

    @NonNull
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;

    public NexusCalculationSummary calculate(List<Transaction> transactions, NexusStateRule nexusStateRule) {
        log.debug("Calculating amount and count for all transactions on timeframe : " + nexusStateRule.getTimeFrame());

        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);
        long count = 0;
        float amount = 0;

        for (Transaction filteredTransaction : filteredTransactions) {
            count += nexusTransactionCountExtractor.extract(filteredTransaction, nexusStateRule);
            amount += nexusAmountAggregatorFactory.createNexusTransactionAmountAggregator(filteredTransaction, nexusStateRule).aggregate();
        }

        log.debug("Calculated total amount of : " + amount + ", and count : " + count);
        return new NexusCalculationSummary(count, amount);
    }

}