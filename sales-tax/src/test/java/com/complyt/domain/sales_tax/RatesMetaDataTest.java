package com.complyt.domain.sales_tax;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class RatesMetaDataTest {

    RatesMetaData ratesMetaData;

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
        ratesMetaData = new RatesMetaData(new BigDecimal("0.01"), new BigDecimal("0.01"), BigDecimal.ZERO);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "RatesMetaData[cityDistrictRate=" + ratesMetaData.cityDistrictRate() +
                ", countyDistrictRate=" + ratesMetaData.countyDistrictRate() + ", specialDistrictRate=0]";

        // When
        String actualString = ratesMetaData.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}
