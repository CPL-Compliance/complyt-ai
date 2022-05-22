package com.complyt.domain.sales_tax;

import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.domain.Item;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxCalculatorTest {

    @InjectMocks
    SalesTaxCalculator salesTaxCalculator;

    @Test
    void calculate_SalesTaxBeingCalculated_SalesTaxAmountReturned(){
        // Given
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f);
        List<Item> items = new ArrayList<Item>(){{
                add(new Item(1000,2,2000,"description","name","taxCode"));
                add(new Item(3000,3,9000,"description","name","taxCode"));
        }};
        float amount = 0;

        // When
        for(Item item : items){
            amount += salesTaxRate.getTaxRate() * item.getUnitPrice() * item.getQuantity();
        }
        SalesTax salesTax = new SalesTax(salesTaxRate,amount);
        SalesTax salesTaxReturnedFromCalculation = salesTaxCalculator.calculate(salesTaxRate,items);

        // Then
        assertNotNull(salesTaxReturnedFromCalculation);
        assertEquals(salesTax,salesTaxReturnedFromCalculation);

    }

}
