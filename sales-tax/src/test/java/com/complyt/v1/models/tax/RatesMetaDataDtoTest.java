package com.complyt.v1.models.tax;

import com.complyt.security.TenantResolver;
import com.complyt.v1.models.tax.sales_tax.RatesMetaDataDto;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RatesMetaDataDtoTest {

    private UnitTestUtilities testUtilities;
    private RatesMetaDataDto ratesMetaDataDto;
    private RatesMetaDataDto anotherRatesMetaData;


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
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        ratesMetaDataDto = testUtilities.createRatesMetaDataDto();
        anotherRatesMetaData = testUtilities.createRatesMetaDataDto();
    }

    @Test
    void equals_IdenticalRecords_Equal() {
        assertEquals(ratesMetaDataDto, anotherRatesMetaData);
    }

    @Test
    void equals_NotIdenticalRecords_NotEqual() {
        // Given
        ratesMetaDataDto = new RatesMetaDataDto(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO);

        // Then
        assertNotEquals(ratesMetaDataDto, anotherRatesMetaData);
    }

    @Test
    void hashCode_IdenticalRecords_Equal() {
        assertEquals(ratesMetaDataDto.hashCode(), anotherRatesMetaData.hashCode());
    }

    @Test
    void hashCode_NotIdenticalRecords_Equal() {
        // Given
        ratesMetaDataDto = new RatesMetaDataDto(BigDecimal.ONE, BigDecimal.ZERO, BigDecimal.ZERO);
        assertNotEquals(ratesMetaDataDto.hashCode(), anotherRatesMetaData.hashCode());
    }

    @Test
    void toString_StringMatches_Equal() {
        String salesTaxDtoToString = "RatesMetaDataDto" + "[" +
                "cityDistrictRate=" + ratesMetaDataDto.cityDistrictRate() +
                ", countyDistrictRate=" + ratesMetaDataDto.countyDistrictRate() +
                ", specialDistrictRate=0]";
        assertEquals(ratesMetaDataDto.toString(), salesTaxDtoToString);
    }



}