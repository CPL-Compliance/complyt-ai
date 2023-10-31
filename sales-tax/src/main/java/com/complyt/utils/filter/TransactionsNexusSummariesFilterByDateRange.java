package com.complyt.utils.filter;

import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.utils.factory.DateRange;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class TransactionsNexusSummariesFilterByDateRange implements ListFilter<TransactionNexusSummary, DateRange> {

    @Override
    public List<TransactionNexusSummary> filter(List<TransactionNexusSummary> transactionNexusSummaries, DateRange dateRange) {
        return transactionNexusSummaries.stream()
                .filter(transactionNexusSummary -> isNexusCalculationRequiredForTransaction(dateRange, transactionNexusSummary))
                .toList();
    }

    private boolean isNexusCalculationRequiredForTransaction(DateRange dateRange, TransactionNexusSummary transactionNexusSummary) {
        return transactionNexusSummary.externalCreatedDate().isAfter(dateRange.getStart().minusNanos(1)) &&
               transactionNexusSummary.externalCreatedDate().isBefore(dateRange.getEnd().plusNanos(1));
    }
}
