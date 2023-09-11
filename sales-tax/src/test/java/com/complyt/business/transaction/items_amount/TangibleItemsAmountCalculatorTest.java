package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.TangibleItemsAmountCalculator;
import com.complyt.domain.transaction.Item;
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

public class TangibleItemsAmountCalculatorTest {

    private TangibleItemsAmountCalculator tangibleItemsAmountCalculator;

    List<Taxable> items;

    @BeforeEach
    void setUp() {
        items = createItems();
        tangibleItemsAmountCalculator = new TangibleItemsAmountCalculator();
    }

    private List<Taxable> createItems() {
        return new ArrayList<>() {
            {
                SalesTaxRates salesTaxRates = new SalesTaxRates(new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), new BigDecimal("0.5"), null);
                add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1",
                        null, salesTaxRates, false, BigDecimal.ZERO, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
                add(new Item(new BigDecimal(5000), new BigDecimal(4), new BigDecimal(20000), "description", "name", "C1S3",
                        null, salesTaxRates, false, BigDecimal.ZERO, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
                add(new Item(new BigDecimal(5000), new BigDecimal(4), new BigDecimal(20000), "description", "name", "C1S2",
                        null, salesTaxRates, false, BigDecimal.ZERO, TangibleCategory.INTANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };
    }

    @Test
    void calculate_TwoItemsAreTangible_ReturnsAmountOfTwoItems() {
        // Before
        BigDecimal expectedAmount = items.get(0).getTotalPrice().add(items.get(1).getTotalPrice());

        // When + Then
        BigDecimal actualAmount = tangibleItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_OneItemIsTangible_ReturnsAmountOfOneItem() {
        // Before
        items.set(0, items.get(0).withTangibleCategory(TangibleCategory.INTANGIBLE));
        BigDecimal expectedAmount = items.get(1).getTotalPrice();

        // When + Then
        BigDecimal actualAmount = tangibleItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_NoTangibleItems_Returns0() {
        // Before
        items.set(0, items.get(0).withTangibleCategory(TangibleCategory.INTANGIBLE));
        items.set(1, items.get(1).withTangibleCategory(TangibleCategory.INTANGIBLE));
        BigDecimal expectedAmount = BigDecimal.ZERO;

        // When + Then
        BigDecimal actualAmount = tangibleItemsAmountCalculator.calculate(items);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Taxable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> tangibleItemsAmountCalculator.calculate(nullItems));

        // Then
        assertEquals("items is marked non-null but is null", exception.getMessage());
    }
}
