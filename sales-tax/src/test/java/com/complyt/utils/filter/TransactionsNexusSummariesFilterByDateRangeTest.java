package com.complyt.utils.filter;

import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.query.DateRangeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionsNexusSummariesFilterByDateRangeTest {

    private UnitTestUtilities unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant");

    private TransactionsNexusSummariesFilterByDateRange transactionsNexusSummariesFilterByDateRange;

    @BeforeEach
    void setup() {
        transactionsNexusSummariesFilterByDateRange = new TransactionsNexusSummariesFilterByDateRange();
    }


    @Test
    void filter_SomeTransactionFilter_ReturnsFilteredTransactions() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        DateRange dateRange = new DateRangeStrategy(TimeFrame.PREVIOUS_TWELVE_MONTHS, now, now).getDateRange();

        List<TransactionNexusSummary> transactionNexusSummaries = List.of(
                unitTestUtilities.createTransactionNexusSummary(),
                unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getStart()),
                unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getEnd()),
                unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getEnd().plusNanos(1)),
                unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getStart().minusNanos(1)),
                unitTestUtilities.createTransactionNexusSummary().withTransactionType(TransactionType.REFUND));

        List<TransactionNexusSummary> expectedList = List.of(unitTestUtilities.createTransactionNexusSummary(),
                unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getStart()),
                unitTestUtilities.createTransactionNexusSummary().withExternalCreatedDate(dateRange.getEnd()),
                unitTestUtilities.createTransactionNexusSummary().withTransactionType(TransactionType.REFUND));

        // Then
        assertEquals(expectedList, transactionsNexusSummariesFilterByDateRange.filter(transactionNexusSummaries, dateRange));
    }

}