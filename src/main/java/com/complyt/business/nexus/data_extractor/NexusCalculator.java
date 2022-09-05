package com.complyt.business.nexus.data_extractor;

import com.complyt.business.filter.TransactionsFilterByNexusRules;
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

    @NonNull
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;

    public NexusCalculationSummary calculate(List<Transaction> transactions, NexusStateRule nexusStateRule) {
        log.debug("Calculating amount and count for all transactions on timeframe : " + nexusStateRule.getTimeFrame());

        long count = 0;
        float amount = 0;
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);

        for (Transaction filteredTransaction : filteredTransactions) {
            count += nexusTransactionCountExtractor.extract(filteredTransaction, nexusStateRule);
            amount += nexusTransactionAmountExtractor.extract(filteredTransaction, nexusStateRule);
        }

        log.debug("Calculated total amount of : " + amount + ", and count : " + count);
        return new NexusCalculationSummary(count, amount);
    }

}
