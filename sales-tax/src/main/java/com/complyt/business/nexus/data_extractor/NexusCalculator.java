package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
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
    private NexusTransactionsAmountCalculator nexusTransactionsAmountCalculator;

    @NonNull
    private NexusTransactionsCountCalculator nexusTransactionsCountCalculator;

    @NonNull
    private TransactionsFilterByNexusRules transactionsFilterByNexusRules;

    public NexusCalculationSummary calculate(List<Transaction> transactions, NexusStateRule nexusStateRule) {
        log.debug("Calculating amount and count for all transactions on timeframe : " + nexusStateRule.getTimeFrame());

        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);

        long count = nexusTransactionsCountCalculator.extract(filteredTransactions, nexusStateRule);
        float amount = nexusTransactionsAmountCalculator.extract(filteredTransactions, nexusStateRule);

        log.debug("Calculated total amount of : " + amount + ", and count : " + count);
        return new NexusCalculationSummary(count, amount);
    }

}