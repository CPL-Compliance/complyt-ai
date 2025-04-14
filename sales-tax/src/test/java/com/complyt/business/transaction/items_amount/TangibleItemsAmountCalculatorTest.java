package com.complyt.business.transaction.items_amount;

import com.complyt.business.transaction.items_amounts.TangibleItemsAmountCalculator;
import com.complyt.domain.Taxable;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.transaction.Item;
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

public class TangibleItemsAmountCalculatorTest {

    private UnitTestUtilities testUtilities;
    private TangibleItemsAmountCalculator tangibleItemsAmountCalculator;

    List<Taxable> items;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());

        items = new ArrayList<>(testUtilities.setCalculatedTotalOnItemList(testUtilities.createItems(true, false, true)));
        tangibleItemsAmountCalculator = new TangibleItemsAmountCalculator();
    }

    private List<Taxable> createItems() {
        List<Item> itemList = testUtilities.createItemsWithSalesTaxRate(false, false, true);
        itemList.add(2, itemList.get(0).withTangibleCategory(TangibleCategory.INTANGIBLE));

        return new ArrayList<>(itemList);
    }

    @Test
    void calculate_TwoItemsAreTangible_ReturnsAmountOfTwoItems() {
        // Before
        BigDecimal expectedAmount = items.get(0).getCalculatedTotal().add(items.get(1).getCalculatedTotal());

        // When + Then
        BigDecimal actualAmount = tangibleItemsAmountCalculator.calculate(items, false);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_OneItemIsTangible_ReturnsAmountOfOneItem() {
        // Before
        items.set(0, items.get(0).withTangibleCategory(TangibleCategory.INTANGIBLE));
        BigDecimal expectedAmount = items.get(1).getCalculatedTotal();

        // When + Then
        BigDecimal actualAmount = tangibleItemsAmountCalculator.calculate(items, false);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    void calculate_NoTangibleItems_Returns0() {
        // Before
        items.set(0, items.get(0).withTangibleCategory(TangibleCategory.INTANGIBLE));
        items.set(1, items.get(1).withTangibleCategory(TangibleCategory.INTANGIBLE));
        BigDecimal expectedAmount = BigDecimal.ZERO;

        // When + Then
        BigDecimal actualAmount = tangibleItemsAmountCalculator.calculate(items, false);
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassed_ThrowsException() {
        // Given
        List<Taxable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> tangibleItemsAmountCalculator.calculate(nullItems, false));

        // Then
        assertEquals("items is marked non-null but is null", exception.getMessage());
    }

    @Test
    void calculate_TwoItemsAreTangibleWithTaxInclusive_ReturnsAmountOfTwoItems() {
        // Before
        items = testUtilities.createTaxablesWithSalesTaxRate(true,true,true);
        BigDecimal expectedAmount = BigDecimal.valueOf(1428.571428);

        // When
        BigDecimal actualAmount = tangibleItemsAmountCalculator.calculate(items, true);

        // Then
        assertEquals(expectedAmount, actualAmount);
    }

    @Test
    public void calculate_NullItemsPassedAndTaxInclusive_ThrowsException() {
        // Given
        List<Taxable> nullItems = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> tangibleItemsAmountCalculator.calculate(nullItems, true));

        // Then
        assertEquals("items is marked non-null but is null", exception.getMessage());
    }
}
