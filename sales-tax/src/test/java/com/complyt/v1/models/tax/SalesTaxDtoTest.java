package com.complyt.v1.models.tax;

import com.complyt.security.TenantResolver;
import com.complyt.v1.models.tax.sales_tax.SalesTaxDto;
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
public class SalesTaxDtoTest {

    private SalesTaxDto salesTaxDto;
    private SalesTaxDto anotherSalesTaxDto;

    private UnitTestUtilities testUtilities;


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
        // Given
        salesTaxDto = testUtilities.createSalesTaxDtoWithAllFields();


        // Then
        anotherSalesTaxDto = testUtilities.createSalesTaxDtoWithAllFields();
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        assertEquals(salesTaxDto, anotherSalesTaxDto);
    }

    @Test
    void equals_NotIdenticalCustomers_NotEqual() {
        // Given
        BigDecimal newAmount = salesTaxDto.amount().subtract(BigDecimal.ONE);
        anotherSalesTaxDto = salesTaxDto.withAmount(newAmount);

        // Then
        assertNotEquals(salesTaxDto, anotherSalesTaxDto);
    }

    @Test
    void hashCode_IdenticalSalesTax_Equal() {
        assertEquals(salesTaxDto.hashCode(), anotherSalesTaxDto.hashCode());
    }

    @Test
    void hashCode_NotIdenticalSalesTax_NotEqual() {
        // Given
        BigDecimal newAmount = salesTaxDto.amount().subtract(BigDecimal.ONE);
        anotherSalesTaxDto = salesTaxDto.withAmount(newAmount);

        // Then
        assertNotEquals(salesTaxDto.hashCode(), anotherSalesTaxDto.hashCode());
    }

    @Test
    void toString_StringMatches_Equal() {
        String salesTaxDtoToString = "SalesTaxDto[" +
                "complytId=null" +
                ", amount=0" +
                ", rate=0" +
                ", salesTaxRates=SalesTaxRatesDto[" +
                "stateRate=null" +
                ", countyRate=0.1" +
                ", cityRate=0.1" +
                ", combinedDistrictRate=0.1" +
                ", ratesMetaData=null" +
                ", mtaRate=0" +
                ", spdRate=0" +
                ", otherRate=0" +
                ", taxRate=0.1]" +
                ", gtRates=GtRatesDto[" +
                "countryRate=0.1" +
                ", regionRate=0.1" +
                ", taxRate=0.2]" +
                "]";

        assertEquals(salesTaxDto.toString(), salesTaxDtoToString);
    }

}
