package com.complyt.v1.models.sales_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxDtoTest {

    private SalesTaxDto salesTaxDto;
    private SalesTaxDto anotherSalesTaxDto;

    private UnitTestUtilities testUtilities;


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
        String salesTaxDtoToString = "SalesTaxDto" + "[" +
                "amount=" + salesTaxDto.amount() +
                ", rate=" + salesTaxDto.rate() +
                ", salesTaxRates=" + salesTaxDto.salesTaxRates() +
                ", gtRates=" + salesTaxDto.gtRates() +
                "]";
        assertEquals(salesTaxDto.toString(), salesTaxDtoToString);
    }

}
