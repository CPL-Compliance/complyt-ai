package com.example.complyt.business.mapper;

import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.mappers.SalesTaxDataToSalesTaxRateMapper;
import testUtils.TestUtilities;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesTaxDataToSalesTaxRateTest {

    @InjectMocks
    SalesTaxDataToSalesTaxRate salesTaxDataToSalesTaxRate;

    @Mock
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    @Mock
    SalesTaxData salesTaxData;

    @Test
    void map_MapsIncorporatedAddress_ReturnsSalesTaxRate() {
        // Given
        SalesTaxRates expectedSalesTaxRates = TestUtilities.createCaliforniaSalesTaxRates();

        // When
        when(salesTaxDataToSalesTaxRateMapper.map(salesTaxData)).thenReturn(expectedSalesTaxRates);
        when(salesTaxData.isUnincorporated()).thenReturn(false);

        Mono<SalesTaxRates> actualSalesTaxRate = salesTaxDataToSalesTaxRate.map(salesTaxData);

        // Then
        StepVerifier.create(actualSalesTaxRate).expectNext(expectedSalesTaxRates).verifyComplete();
    }

    @Test
    void map_MapsUnincorporatedAddress_ReturnsSalesTaxRateWithCityRatesAsZeros() {
        // Given
        SalesTaxRates salesTaxRate = TestUtilities.createCaliforniaSalesTaxRates();
        SalesTaxRates expectedSalesTaxRate = salesTaxRate.withCityRate(0).withCityDistrictRate(0);

        // When
        when(salesTaxDataToSalesTaxRateMapper.map(salesTaxData)).thenReturn(salesTaxRate);
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
