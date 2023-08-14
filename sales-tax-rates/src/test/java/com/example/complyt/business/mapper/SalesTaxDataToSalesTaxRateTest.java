package com.example.complyt.business.mapper;

import com.complyt.business.mapper.SalesTaxDataToSalesTaxRate;
import com.complyt.domain.RatesMetaData;
import com.complyt.domain.SalesTaxData;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.mappers.SalesTaxDataToSalesTaxRateMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

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
        SalesTaxRates salesTaxRates = TestUtilities.createCaliforniaSalesTaxRates();
        double modifiedTaxRate = salesTaxRates.taxRate() - salesTaxRates.cityRate();
        SalesTaxRates expectedSalesTaxRate =
                salesTaxRates.withTaxRate(modifiedTaxRate).withCityRate(0);

        // When
        when(salesTaxDataToSalesTaxRateMapper.map(salesTaxData)).thenReturn(salesTaxRates);
        when(salesTaxData.isUnincorporated()).thenReturn(true);

        Mono<SalesTaxRates> actualSalesTaxRate = salesTaxDataToSalesTaxRate.map(salesTaxData);

        // Then
        StepVerifier.create(actualSalesTaxRate).expectNext(expectedSalesTaxRate).verifyComplete();
    }

    @Test
    void map_MapsUnincorporatedAddressWithRatesMetaData_ReturnsSalesTaxRateWithCityRatesAsZeros() {
        // Given
        RatesMetaData ratesMetaData = new RatesMetaData(0.01f, 0.01f);
        SalesTaxRates salesTaxRates = TestUtilities.createCaliforniaSalesTaxRates().withRatesMetaData(ratesMetaData);
        double modifiedTaxRate = salesTaxRates.taxRate() - salesTaxRates.cityRate() - salesTaxRates.ratesMetaData().cityDistrictRate();
        RatesMetaData expectedRatesMetaData = ratesMetaData.withCityDistrictRate(0);
        SalesTaxRates expectedSalesTaxRate =
                salesTaxRates.withTaxRate(modifiedTaxRate).withCityRate(0).withRatesMetaData(expectedRatesMetaData);

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

        assertEquals(nullPointerException.getMessage(), "salesTaxData " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }
}
