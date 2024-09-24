package com.complyt.v1.models.transaction;

import com.complyt.domain.currency.CurrencySource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExchangeRateInfoDtoTest {

    private ExchangeRateInfoDto exchangeRateInfoDto;
    private UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        exchangeRateInfoDto = testUtilities.createExchangeRateInfoDto(BigDecimal.valueOf(1000), BigDecimal.valueOf(100), BigDecimal.valueOf(1100),"EUR", "USD", BigDecimal.ONE, CurrencySource.COMPLYT, false, LocalDateTime.parse("2024-09-01T00:00:00"));
    }

    @Test
    void Equals_sameItemDto_ReturnsTrue() {
        // Given
        ExchangeRateInfoDto givenExchangeRateInfoDto = testUtilities.createExchangeRateInfoDto(BigDecimal.valueOf(1000), BigDecimal.valueOf(100),BigDecimal.valueOf(1100),"EUR", "USD", BigDecimal.ONE, CurrencySource.COMPLYT, false, LocalDateTime.parse("2024-09-01T00:00:00"));


        // When
        boolean isEquals = exchangeRateInfoDto.equals(givenExchangeRateInfoDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ExchangeRateInfoDto[totalItemsAmountInUSD=" + exchangeRateInfoDto.totalItemsAmountInUSD() +
                ", transactionSalesTaxInUsd=" + exchangeRateInfoDto.transactionSalesTaxInUsd() +
                ", finalTransactionAmountInUsd=" + exchangeRateInfoDto.finalTransactionAmountInUsd() +
                ", fromCurrency=" + exchangeRateInfoDto.fromCurrency() +
                ", toCurrency=" + exchangeRateInfoDto.toCurrency() +
                ", fxRate=" + exchangeRateInfoDto.fxRate() +
                ", source=" + exchangeRateInfoDto.source() +
                ", isExchangeRateEstimated=" + exchangeRateInfoDto.isExchangeRateEstimated() +
                ", exchangeRateDate=" + exchangeRateInfoDto.exchangeRateDate() + "]";

        // When
        String actualString = exchangeRateInfoDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}