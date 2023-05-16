package com.complyt.business.sales_tax.mapper;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.mappers.ComplytSalesTaxRatesToSalesTaxRatesMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ComplytSalesTaxRatesToSalesTaxRatesTest {

    @InjectMocks
    ComplytSalesTaxRatesToSalesTaxRates salesTaxDataToSalesTaxRate;

    @Mock
    ComplytSalesTaxRatesToSalesTaxRatesMapper complytSalesTaxRatesToSalesTaxRatesMapper;

    @Mock
    ComplytSalesTaxRates c;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }


    @Test
    void map_Maps_ReturnsSalesTaxRateWithCityRatesAsZeros() {
        // Given
        SalesTaxRates expectedSalesTaxRate = testUtilities.createSalesTaxRates();

        // When
        when(complytSalesTaxRatesToSalesTaxRatesMapper.map(c)).thenReturn(expectedSalesTaxRate);

        Mono<SalesTaxRates> actualSalesTaxRate = salesTaxDataToSalesTaxRate.map(c);

        // Then
        StepVerifier.create(actualSalesTaxRate).expectNext(expectedSalesTaxRate).verifyComplete();
    }

    @Test
    void map_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        ComplytSalesTaxRates nullComplytSalesTaxRates = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxDataToSalesTaxRate.map(nullComplytSalesTaxRates);
        });

        assertEquals(nullPointerException.getMessage(), "complytSalesTaxRates is marked non-null but is null");
    }
}
