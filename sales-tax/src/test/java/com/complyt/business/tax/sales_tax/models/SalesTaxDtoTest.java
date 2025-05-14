package com.complyt.business.tax.sales_tax.models;

import com.complyt.security.TenantResolver;
import com.complyt.v1.models.tax.global_tax.GtRatesDto;
import com.complyt.v1.models.tax.sales_tax.RatesMetaDataDto;
import com.complyt.v1.models.tax.sales_tax.SalesTaxRatesDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mockStatic;

class SalesTaxDtoTest {
    SalesTaxDto salesTaxDto;



    @BeforeEach
    void setUp() {
        SalesTaxRatesDto salesTaxRates = new SalesTaxRatesDto(
                new BigDecimal("0.05"),
                new BigDecimal("0.02"),
                new BigDecimal("0.01"),
                new BigDecimal("0.08"),
                new RatesMetaDataDto(new BigDecimal("0.01"), new BigDecimal("0.02"), BigDecimal.ZERO),
                new BigDecimal("0.03"),
                new BigDecimal("0.01"),
                new BigDecimal("0.02"),
                new BigDecimal("0.09")
        );

        GtRatesDto gtRates = new GtRatesDto(
                new BigDecimal("0.01"),
                new BigDecimal("0.02"),
                new BigDecimal("0.03")
        );

        salesTaxDto = new SalesTaxDto(
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                new BigDecimal("0.09"),
                salesTaxRates,
                gtRates
        );
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "SalesTaxDto[complytId=" + salesTaxDto.complytId() +
                ", amount=" + salesTaxDto.amount() +
                ", rate=" + salesTaxDto.rate() +
                ", salesTaxRates=" + salesTaxDto.salesTaxRates() +
                ", gtRates=" + salesTaxDto.gtRates() + "]";

        // When
        String actualString = salesTaxDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withAmount_ReturnNewInstance() {
        // Given
        BigDecimal newAmount = new BigDecimal("200.00");

        // When
        SalesTaxDto updatedDto = salesTaxDto.withAmount(newAmount);

        // Then
        assertNotEquals(salesTaxDto, updatedDto);
        assertEquals(newAmount, updatedDto.amount());
        assertEquals(salesTaxDto.rate(), updatedDto.rate()); // Ensure immutability
    }

    @Test
    void withSalesTaxRates_ReturnNewInstance() {
        // Given
        SalesTaxRatesDto newRates = new SalesTaxRatesDto(
                new BigDecimal("0.06"),
                new BigDecimal("0.03"),
                new BigDecimal("0.02"),
                new BigDecimal("0.09"),
                new RatesMetaDataDto(new BigDecimal("0.02"), new BigDecimal("0.03"), BigDecimal.ZERO),
                new BigDecimal("0.04"),
                new BigDecimal("0.02"),
                new BigDecimal("0.03"),
                new BigDecimal("0.10")
        );

        // When
        SalesTaxDto updatedDto = salesTaxDto.withSalesTaxRates(newRates);

        // Then
        assertNotEquals(salesTaxDto, updatedDto);
        assertEquals(newRates, updatedDto.salesTaxRates());
        assertEquals(salesTaxDto.amount(), updatedDto.amount()); // Ensure immutability
    }
}