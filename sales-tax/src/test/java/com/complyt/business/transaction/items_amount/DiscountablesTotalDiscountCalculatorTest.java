package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.DiscountablesTotalDiscountCalculator;
import com.complyt.business.transaction.items_amounts.TotalItemsAmountCalculator;
import com.complyt.domain.Discountable;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DiscountablesTotalDiscountCalculatorTest {
    private UnitTestUtilities testUtilities;

    List<Discountable> items;

    private DiscountablesTotalDiscountCalculator discountablesTotalDiscountCalculator;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        items = new ArrayList<>(testUtilities.createItems(true, false,true));
        discountablesTotalDiscountCalculator = new DiscountablesTotalDiscountCalculator();
    }

    @Test
    void calculate_TwoItemsWithNoDiscount_Returns0() {
        // Given
        BigDecimal expectedAmount = BigDecimal.ZERO;

        // When + Then
        BigDecimal actualAmount = discountablesTotalDiscountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_TwoItemsWithDiscount_ReturnsAmountOfTwoItemsDiscount() {
        // Given

        List<Item> itemList = testUtilities.createItems(true, false,true);
        List<Discountable> itemListWith2Discounts = List.of(
                itemList.get(0).withDiscount(BigDecimal.valueOf(500)),
                itemList.get(1).withDiscount(BigDecimal.valueOf(400))
        );

        BigDecimal expectedAmount = itemListWith2Discounts.get(0).getDiscount()
                .add(itemListWith2Discounts.get(1).getDiscount());

        // When + Then
        BigDecimal actualAmount = discountablesTotalDiscountCalculator.calculate(itemListWith2Discounts);
        assertEquals(expectedAmount, actualAmount);
    }

 @Test
    void calculate_OneItemsWithDiscount_ReturnsAmountOfOneItemsDiscount() {
        // Given

        List<Item> itemList = testUtilities.createItems(true, false,true);
        List<Discountable> itemListWith1Discount = List.of(
                itemList.get(0).withDiscount(BigDecimal.valueOf(500)),
                itemList.get(1).withDiscount(null)
        );

        BigDecimal expectedAmount = itemListWith1Discount.get(0).getDiscount();

        // When + Then
        BigDecimal actualAmount = discountablesTotalDiscountCalculator.calculate(itemListWith1Discount);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Discountable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(
                NullPointerException.class, () -> discountablesTotalDiscountCalculator.calculate(nullItems));

        // Then
        assertEquals("discountables is marked non-null but is null", exception.getMessage());
    }
}