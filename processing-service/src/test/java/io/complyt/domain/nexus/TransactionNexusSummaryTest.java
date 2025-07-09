package io.complyt.domain.nexus;

import io.complyt.domain.transaction.TransactionType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionNexusSummaryTest {

    @Test
    void testConstructorAndAccessors() {
        BigDecimal amount = new BigDecimal("150.00");
        LocalDateTime timestamp = LocalDateTime.of(2023, 12, 25, 10, 30);
        TransactionType type = TransactionType.INVOICE;

        TransactionNexusSummary summary = new TransactionNexusSummary(amount, timestamp, type);

        assertThat(summary.relevantAmount()).isEqualByComparingTo("150.00");
        assertThat(summary.externalCreatedDate()).isEqualTo(timestamp);
        assertThat(summary.transactionType()).isEqualTo(TransactionType.INVOICE);
    }

    @Test
    void testWithMethods() {
        TransactionNexusSummary original = new TransactionNexusSummary(
                new BigDecimal("100.00"),
                LocalDateTime.of(2023, 1, 1, 0, 0),
                TransactionType.INVOICE
        );

        TransactionNexusSummary updatedAmount = original.withRelevantAmount(new BigDecimal("200.00"));
        TransactionNexusSummary updatedDate = original.withExternalCreatedDate(LocalDateTime.of(2023, 5, 5, 5, 5));
        TransactionNexusSummary updatedType = original.withTransactionType(TransactionType.REFUND);

        assertThat(updatedAmount.relevantAmount()).isEqualByComparingTo("200.00");
        assertThat(updatedDate.externalCreatedDate()).isEqualTo(LocalDateTime.of(2023, 5, 5, 5, 5));
        assertThat(updatedType.transactionType()).isEqualTo(TransactionType.REFUND);

        // Ensure immutability
        assertThat(original.relevantAmount()).isEqualByComparingTo("100.00");
        assertThat(original.transactionType()).isEqualTo(TransactionType.INVOICE);
    }
}
