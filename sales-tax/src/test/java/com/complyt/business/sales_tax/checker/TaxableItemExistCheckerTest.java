package com.complyt.business.sales_tax.checker;

import com.complyt.domain.Item;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaxableItemExistCheckerTest {

    TaxableItemExistChecker taxableItemExistenceCheck;

    @BeforeEach
    void setUp() {
        taxableItemExistenceCheck = new TaxableItemExistChecker();
    }

    private Item createTaxableItem() {
        return new Item(2000, 4, 8000, "description", "name", "C1S1",
                null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
        );
    }

    private Item createNotTaxableItem() {
        return new Item(1000, 5, 5000, "description", "name", "C2S1",
                null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.NOT_TAXABLE
        );
    }

    @Test
    void hasTaxableItem_ListHasTaxableItem_ReturnsTrue() {
        // Given
        Item taxableItem = createTaxableItem();
        Item secondItem = taxableItem.withTaxCode("C2S1").withTaxableCategory(TaxableCategory.NOT_TAXABLE);
        List<Item> items = new ArrayList<>() {{
            add(taxableItem);
            add(secondItem);
        }};

        // When + Then
        boolean hasTaxable = taxableItemExistenceCheck.check(items);
        assertTrue(hasTaxable);
    }

    @Test
    void hasTaxableItem_ListDoesNotHaveTaxableItem_ReturnsFalse() {
        // Given
        Item notTaxableItem = createNotTaxableItem();
        List<Item> items = new ArrayList<>() {{
            add(notTaxableItem);
        }};

        // When + Then
        boolean hasTaxable = taxableItemExistenceCheck.check(items);
        assertFalse(hasTaxable);
    }

    @Test
    void hasTaxableItem_NullItemsListPassed_ThrowsException() {
        // Given
        List<Item> nullItems = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            taxableItemExistenceCheck.check(nullItems);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "items is marked non-null but is null");
    }
}
