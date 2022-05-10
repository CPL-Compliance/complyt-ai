package com.complyt.v1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxDtoTest {

    private SalesTaxDto salesTaxDto;
    private SalesTaxDto anotherSalesTaxDto;

    @BeforeEach
    void setUp(){
        // Given
        salesTaxDto = new SalesTaxDto(new SalesTaxRateDto(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),5000);

        // Then
        anotherSalesTaxDto = new SalesTaxDto(salesTaxDto.getSalesTaxRate(), salesTaxDto.getAmount());
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        assertEquals(salesTaxDto,anotherSalesTaxDto);
    }

    @Test
    void equals_NotIdenticalCustomers_NotEqual() {
        // Given
        float newAmount = salesTaxDto.getAmount() - 1;
        anotherSalesTaxDto = salesTaxDto.withAmount(newAmount);

        // Then
        assertNotEquals(salesTaxDto,anotherSalesTaxDto);
    }

    @Test
    void hashCode_IdenticalSalesTax_Equal() {
        assertEquals(salesTaxDto.hashCode(), anotherSalesTaxDto.hashCode());
    }


    @Test
    void toString_StringMatches_Equal(){
        String salesTaxDtoToString = "SalesTaxDto(salesTaxRate=" + salesTaxDto.getSalesTaxRate() + ", amount=" + salesTaxDto.getAmount() + ")";;
        assertEquals(salesTaxDtoToString,salesTaxDto.toString());
    }

}
