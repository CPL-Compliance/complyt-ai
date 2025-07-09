package io.complyt.domain.transaction;

import io.complyt.domain.currency.CurrencySource;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRateInfoTest {

    @Test
    void testFieldAccessors() {
        LocalDateTime now = LocalDateTime.now();
        ExchangeRateInfo info = new ExchangeRateInfo(
                BigDecimal.valueOf(100),
                BigDecimal.valueOf(10),
                BigDecimal.valueOf(110),
                "EUR",
                "USD",
                BigDecimal.valueOf(1.1),
                CurrencySource.COMPLYT,
                true,
                now
        );

        assertEquals(BigDecimal.valueOf(100), info.totalItemsAmountInUsd());
        assertEquals(BigDecimal.valueOf(10), info.transactionSalesTaxInUsd());
        assertEquals(BigDecimal.valueOf(110), info.finalTransactionAmountInUsd());
        assertEquals("EUR", info.fromCurrency());
        assertEquals("USD", info.toCurrency());
        assertEquals(BigDecimal.valueOf(1.1), info.fxRate());
        assertEquals(CurrencySource.COMPLYT, info.source());
        assertTrue(info.isExchangeRateEstimated());
        assertEquals(now, info.exchangeRateDate());
    }

    @Test
    void testWithMethods() {
        ExchangeRateInfo original = new ExchangeRateInfo(
                BigDecimal.ONE, BigDecimal.TEN, BigDecimal.ZERO,
                "GBP", "USD", BigDecimal.valueOf(1.3),
                CurrencySource.COMPLYT, false,
                LocalDateTime.of(2024, 1, 1, 0, 0)
        );

        ExchangeRateInfo updated = original.withFromCurrency("CAD");
        assertEquals("CAD", updated.fromCurrency());
        assertEquals("USD", updated.toCurrency());
    }

    @Test
    void testEqualsAndHashCode() {
        LocalDateTime now = LocalDateTime.now();
        ExchangeRateInfo a = new ExchangeRateInfo(
                BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN,
                "EUR", "USD", BigDecimal.valueOf(1.1),
                CurrencySource.CLIENT, true, now
        );

        ExchangeRateInfo b = new ExchangeRateInfo(
                BigDecimal.TEN, BigDecimal.ONE, BigDecimal.TEN,
                "EUR", "USD", BigDecimal.valueOf(1.1),
                CurrencySource.CLIENT, true, now
        );

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    void testToString() {
        ExchangeRateInfo info = new ExchangeRateInfo(
                BigDecimal.ONE, BigDecimal.TEN, BigDecimal.TEN,
                "JPY", "USD", BigDecimal.valueOf(0.0091),
                CurrencySource.CLIENT, false,
                LocalDateTime.of(2023, 12, 31, 23, 59)
        );

        String str = info.toString();
        assertTrue(str.contains("JPY"));
        assertTrue(str.contains("USD"));
        assertTrue(str.contains("0.0091"));
    }
}
