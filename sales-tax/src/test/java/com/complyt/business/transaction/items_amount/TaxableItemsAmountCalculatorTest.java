package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.TaxableItemsAmountCalculator;
import com.complyt.domain.Item;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TaxableItemsAmountCalculatorTest {

    private TaxableItemsAmountCalculator taxableItemsAmountCalculator;

    List<Taxable> items;

    @BeforeEach
    void setUp() {
        items = createItems();
        taxableItemsAmountCalculator = new TaxableItemsAmountCalculator();
    }

    private List<Taxable> createItems() {
        return new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "C1S1",
                        null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, null), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
                add(new Item(5000, 4, 20000, "description", "name", "C1S3",
                        null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, null), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
                add(new Item(5000, 4, 20000, "description", "name", "C1S2",
                        null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, null), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
    }

    @Test
    void calculate_TwoItemsAreTaxable_ReturnsAmountOfTwoItems() {
        // Before
        float expectedAmount = items.get(0).getTotalPrice() + items.get(1).getTotalPrice();

        // When + Then
        float actualAmount = taxableItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_OneItemIsTaxable_ReturnsAmountOfOneItem() {
        // Before
        items.set(0, items.get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE));
        float expectedAmount = items.get(1).getTotalPrice();

        // When + Then
        float actualAmount = taxableItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_NoTaxableItems_Returns0() {
        // Before
        items.set(0, items.get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE));
        items.set(1, items.get(1).withTaxableCategory(TaxableCategory.NOT_TAXABLE));
        float expectedAmount = 0;

        // When + Then
        float actualAmount = taxableItemsAmountCalculator.calculate(items);
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
