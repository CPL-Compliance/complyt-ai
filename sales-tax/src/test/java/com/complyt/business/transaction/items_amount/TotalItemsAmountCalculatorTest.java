package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.TotalItemsAmountCalculator;
import com.complyt.domain.Taxable;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;

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
        BigDecimal actualAmount = totalItemsAmountCalculator.calculate(items, false);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Taxable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> totalItemsAmountCalculator.calculate(nullItems, false));

        // Then
        assertEquals("items is marked non-null but is null", exception.getMessage());
    }

    @Test
    void calculate_TwoItemsAreTaxableWithTaxInclusive_ReturnsAmountOfTwoItems() {
        // Before
        items = testUtilities.createTaxablesWithSalesTaxRate(true,true,true);
        BigDecimal expectedAmount = BigDecimal.valueOf(1428.571428);

        // When
        BigDecimal actualAmount = totalItemsAmountCalculator.calculate(items, true);

        // Then
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassedAndTaxInclusive_ThrowsException() {
        // Given
        List<Taxable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> totalItemsAmountCalculator.calculate(nullItems, true));

        // Then
        assertEquals("items is marked non-null but is null", exception.getMessage());
    }

}
