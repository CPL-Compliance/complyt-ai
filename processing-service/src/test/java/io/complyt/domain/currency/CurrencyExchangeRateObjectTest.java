package io.complyt.domain.currency;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CurrencyExchangeRateObjectTest {

    @Test
    void testRecordFieldAssignmentAndAccessors() {
        String currency = "USD";
        LocalDateTime date = LocalDateTime.of(2024, 6, 22, 10, 30);
        BigDecimal rate = new BigDecimal("1.2345");

        CurrencyExchangeRateObject object = new CurrencyExchangeRateObject(currency, date, rate);

        assertThat(object.currency()).isEqualTo("USD");
        assertThat(object.date()).isEqualTo(date);
        assertThat(object.rate()).isEqualByComparingTo("1.2345");
    }

    @Test
    void testEqualityAndHashcode() {
        LocalDateTime date = LocalDateTime.of(2024, 6, 22, 10, 30);
        BigDecimal rate = new BigDecimal("1.2345");

        CurrencyExchangeRateObject obj1 = new CurrencyExchangeRateObject("USD", date, rate);
        CurrencyExchangeRateObject obj2 = new CurrencyExchangeRateObject("USD", date, rate);

        assertThat(obj1).isEqualTo(obj2);
        assertThat(obj1.hashCode()).isEqualTo(obj2.hashCode());
    }

    @Test
    void testToString() {
        LocalDateTime date = LocalDateTime.of(2024, 6, 22, 10, 30);
        BigDecimal rate = new BigDecimal("1.2345");

        CurrencyExchangeRateObject object = new CurrencyExchangeRateObject("USD", date, rate);

        String expected = "CurrencyExchangeRateObject[currency=USD, date=2024-06-22T10:30, rate=1.2345]";
        assertThat(object.toString()).isEqualTo(expected);
    }
}
