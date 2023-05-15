package com.complyt.business.sales_tax.mapper;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
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
public class SalesTaxDataToSalesTaxRateTest {

    @InjectMocks
    ComplytSalesTaxRatesToSalesTaxRates salesTaxDataToSalesTaxRate;

    @Mock
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    @Mock
    SalesTaxData salesTaxData;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    @Test
    void map_MapsIncorporatedAddress_ReturnsSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRate = testUtilities.createSalesTaxRates();

        // When
        when(salesTaxDataToSalesTaxRateMapper.map(salesTaxData)).thenReturn(expectedSalesTaxRate);
        when(salesTaxData.isUnincorporated()).thenReturn(false);

        Mono<SalesTaxRates> actualSalesTaxRate = salesTaxDataToSalesTaxRate.map(salesTaxData);

        // Then
        StepVerifier.create(actualSalesTaxRate).expectNext(expectedSalesTaxRate).verifyComplete();
    }

    @Test
    void map_MapsUnincorporatedAddress_ReturnsSalesTaxRateWithCityRatesAsZeros() {
        // Given
        SalesTaxRates salesTaxRates = testUtilities.createSalesTaxRates();
        SalesTaxRates expectedSalesTaxRate = salesTaxRates.withCityRate(0).withCityDistrictRate(0);

        // When
        when(salesTaxDataToSalesTaxRateMapper.map(salesTaxData)).thenReturn(salesTaxRates);
        when(salesTaxData.isUnincorporated()).thenReturn(true);

        Mono<SalesTaxRates> actualSalesTaxRate = salesTaxDataToSalesTaxRate.map(salesTaxData);

        // Then
        StepVerifier.create(actualSalesTaxRate).expectNext(expectedSalesTaxRate).verifyComplete();
    }

    @Test
    void map_NullSalesTaxDataPassed_ThrowsException() {
        // Given
        SalesTaxData nullSalesTaxData = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxDataToSalesTaxRate.map(nullSalesTaxData);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxData is marked non-null but is null");
    }
}
