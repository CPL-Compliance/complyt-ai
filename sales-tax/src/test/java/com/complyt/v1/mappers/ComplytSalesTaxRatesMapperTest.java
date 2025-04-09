package com.complyt.v1.mappers;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.tax.sales_tax.ComplytSalesTaxRatesDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import static org.mockito.Mockito.mockStatic;

public class ComplytSalesTaxRatesMapperTest {

    ComplytSalesTaxRatesDto expectedComplytSalesTaxRatesDto;
    ComplytSalesTaxRates expectedComplytSalesTaxRates;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        expectedComplytSalesTaxRates = UnitTestUtilities.createCaliforniaComplytSalesTaxRates();
        expectedComplytSalesTaxRatesDto = UnitTestUtilities.createCaliforniaComplytSalesTaxRatesDto();
    }

//    @Test
//    void complytSalesTaxRatesDtoToComplytSalesTaxRates_ComplytSalesTaxRatesDto_returnComplytSalesTaxRates() {
//        // Given
//        ComplytSalesTaxRates actualComplytSalesTaxRates = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesDtoToComplytSalesTaxRates(expectedComplytSalesTaxRatesDto);
//
//        // When + Then
//        Assertions.assertEquals(expectedComplytSalesTaxRates, actualComplytSalesTaxRates);
//    }

//    @Test
//    void complytSalesTaxRatesToComplytSalesTaxRatesDto_ComplytSalesTaxRates_returnComplytSalesTaxRatesDto() {
//        // Given
//        ComplytSalesTaxRatesDto actualComplytSalesTaxRatesDto = ComplytSalesTaxRatesMapper.INSTANCE.complytSalesTaxRatesToComplytSalesTaxRatesDto(expectedComplytSalesTaxRates);
//
//        // When + Then
//        Assertions.assertEquals(expectedComplytSalesTaxRatesDto, actualComplytSalesTaxRatesDto);
//    }
}
