package com.complyt.business.sales_tax.mapper;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

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

    TestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
    }

    @Test
    void map_MapsIncorporatedAddress_ReturnsSalesTaxRate() {
        // Given
        SalesTaxRate expectedSalesTaxRate = testUtilities.createSalesTaxRates();

        // When
        when(salesTaxDataToSalesTaxRateMapper.map(salesTaxData)).thenReturn(expectedSalesTaxRate);
        when(salesTaxData.isUnincorporated()).thenReturn(false);

        Mono<SalesTaxRate> actualSalesTaxRate = salesTaxDataToSalesTaxRate.map(salesTaxData);

        // Then
        StepVerifier.create(actualSalesTaxRate).expectNext(expectedSalesTaxRate).verifyComplete();
    }

    @Test
    void map_MapsUnincorporatedAddress_ReturnsSalesTaxRateWithCityRatesAsZeros() {
        // Given
        SalesTaxRate salesTaxRate = testUtilities.createSalesTaxRates();
        SalesTaxRate expectedSalesTaxRate = salesTaxRate.withCityRate(0).withCityDistrictRate(0);

        // When
        when(salesTaxDataToSalesTaxRateMapper.map(salesTaxData)).thenReturn(salesTaxRate);
        when(salesTaxData.isUnincorporated()).thenReturn(true);

        Mono<SalesTaxRate> actualSalesTaxRate = salesTaxDataToSalesTaxRate.map(salesTaxData);

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
