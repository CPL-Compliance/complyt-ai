package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.TotalItemsAmountCalculator;
import com.complyt.domain.Item;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TotalItemsAmountCalculatorTest {

    private TotalItemsAmountCalculator totalItemsAmountCalculator;

    private List<Taxable> items;

    @BeforeEach
    void setUp() {
        items = createItems();
        totalItemsAmountCalculator = new TotalItemsAmountCalculator();
    }

    private List<Taxable> createItems() {
        return new ArrayList<>() {
            {
                add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1",
                        null, new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null), false, BigDecimal.ZERO, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
                add(new Item(new BigDecimal(5000), new BigDecimal(4), new BigDecimal(20000), "description", "name", "C1S3",
                        null, new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null), false, BigDecimal.ZERO, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
                add(new Item(new BigDecimal(5000), new BigDecimal(4), new BigDecimal(20000), "description", "name", "C1S2",
                        null, new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null), false, BigDecimal.ZERO, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
    }

    @Test
    void calculate_CalculatesTotalItemsAmount_ReturnsTotalAmount() {
        // Given
        BigDecimal expectedAmount = items.get(0).getTotalPrice().add(items.get(1).getTotalPrice()).add(items.get(2).getTotalPrice());

        // When + Then
        BigDecimal actualAmount = totalItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Taxable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> totalItemsAmountCalculator.calculate(nullItems));

        // Then
        assertEquals("items is marked non-null but is null", exception.getMessage());
    }


}
