package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.TotalItemsAmountCalculator;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TotalItemsAmountCalculatorTest {

    private UnitTestUtilities testUtilities;
    private TotalItemsAmountCalculator totalItemsAmountCalculator;

    private List<Taxable> items;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        items = new ArrayList<>(testUtilities.setCalculatedTotalOnItemList(testUtilities.createItems(true, false, true)));
        totalItemsAmountCalculator = new TotalItemsAmountCalculator();
    }

    @Test
    void calculate_CalculatesTotalItemsAmount_ReturnsTotalAmount() {
        // Given
        BigDecimal expectedAmount = items.get(0).getCalculatedTotal().add(items.get(1)
                .getCalculatedTotal());

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
