package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.TaxableItemsAmountCalculator;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.transaction.Item;
import org.apache.commons.math.stat.inference.TestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaxableItemsAmountCalculatorTest {

    UnitTestUtilities unitTestUtilities;
    private TaxableItemsAmountCalculator taxableItemsAmountCalculator;

    List<Taxable> items;

    @BeforeEach
    void setUp() {
//        items = createItems();
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());

        items = new ArrayList<>(unitTestUtilities.setCalculatedTotalOnItemList(unitTestUtilities.createItems(true, true)));
        taxableItemsAmountCalculator = new TaxableItemsAmountCalculator();
    }

    @Test
    void calculate_TwoItemsAreTaxable_ReturnsAmountOfTwoItems() {
        // Before
        BigDecimal expectedAmount = items.get(0).getCalculatedTotal().add(items.get(1).getCalculatedTotal());

        // When + Then
        BigDecimal actualAmount = taxableItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_OneItemIsTaxable_ReturnsAmountOfOneItem() {
        // Before
        items.set(0, items.get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE));
        BigDecimal expectedAmount = items.get(1).getCalculatedTotal();

        // When + Then
        BigDecimal actualAmount = taxableItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_NoTaxableItems_Returns0() {
        // Before
        items.set(0, items.get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE));
        items.set(1, items.get(1).withTaxableCategory(TaxableCategory.NOT_TAXABLE));
        BigDecimal expectedAmount = BigDecimal.ZERO;

        // When + Then
        BigDecimal actualAmount = taxableItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_ItemsWithDiscountPassedBothTaxable_ReturnsAmount() { //todo: good example
        // Before
        List<Taxable> discountedItems = new ArrayList<>(unitTestUtilities.setCalculatedTotalOnItemList(
                unitTestUtilities.createItems(true, true)
                        .stream().map(item -> item
                                .withDiscount(BigDecimal.valueOf(500)))
                        .collect(Collectors.toList())));

        BigDecimal expectedAmount = discountedItems.get(0).getCalculatedTotal().add(discountedItems.get(1).getCalculatedTotal());

        // When + Then
        BigDecimal actualAmount = taxableItemsAmountCalculator.calculate(discountedItems);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Taxable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> taxableItemsAmountCalculator.calculate(nullItems));

        // Then
        assertEquals("items is marked non-null but is null", exception.getMessage());
    }

}
