package io.complyt.domain.nexus;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

class NexusCalculationSummaryTest {

    @Test
    void testConstructorAndAccessors() {
        BigDecimal amount = new BigDecimal("123.45");
        long count = 5L;

        NexusCalculationSummary summary = new NexusCalculationSummary(count, amount);

        assertThat(summary.getCount()).isEqualTo(5);
        assertThat(summary.getAmount()).isEqualByComparingTo("123.45");
        assertThat(summary.amount()).isEqualByComparingTo("123.45");
    }

    @Test
    void testAmountReturnsZeroIfNull() {
        NexusCalculationSummary summary = new NexusCalculationSummary(3L, null);

        assertThat(summary.amount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testBuilderDefaults() {
        NexusCalculationSummary.Builder builder = new NexusCalculationSummary.Builder();
        NexusCalculationSummary summary = builder.build();

        assertThat(summary.getCount()).isZero();
        assertThat(summary.amount()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void testBuilderWithCustomValues() {
        NexusCalculationSummary summary = new NexusCalculationSummary.Builder()
                .setCount(7L)
                .setAmount(new BigDecimal("777.77"))
                .build();

        assertThat(summary.getCount()).isEqualTo(7);
        assertThat(summary.amount()).isEqualByComparingTo("777.77");
    }

    @Test
    void testChainingWithAccessors() {
        NexusCalculationSummary summary = new NexusCalculationSummary(0, BigDecimal.ZERO)
                .setCount(10L)
                .setAmount(new BigDecimal("200.00"));

        assertThat(summary.getCount()).isEqualTo(10);
        assertThat(summary.getAmount()).isEqualByComparingTo("200.00");
    }
}
