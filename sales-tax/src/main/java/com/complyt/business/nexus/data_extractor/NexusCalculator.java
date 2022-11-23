package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.utils.filter.ListFilter;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
@Slf4j
public class NexusCalculator {

    @NonNull
    private NexusTransactionsAmountExtractor nexusTransactionsAmountExtractor;

    @NonNull
    private NexusTransactionsCountExtractor nexusTransactionsCountExtractor;

    @NonNull
    @Qualifier("transactionsFilterByNexusRules")
    private ListFilter<Transaction, NexusStateRule> transactionsFilterByNexusRules;

    public NexusCalculationSummary calculate(List<Transaction> transactions, NexusStateRule nexusStateRule) {
        log.debug("Calculating amount and count for all transactions on timeframe : " + nexusStateRule.getTimeFrame());

        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);

        long count = nexusTransactionsCountExtractor.extract(filteredTransactions, nexusStateRule);
        float amount = nexusTransactionsAmountExtractor.extract(filteredTransactions, nexusStateRule);

        log.debug("Calculated total amount of : " + amount + ", and count : " + count);
        return new NexusCalculationSummary(count, amount);
    }

}