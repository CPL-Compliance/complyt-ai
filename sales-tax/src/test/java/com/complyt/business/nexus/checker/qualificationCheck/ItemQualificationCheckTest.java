package com.complyt.business.nexus.checker.qualificationCheck;

import com.complyt.business.nexus.checker.qualification_check.ItemQualificationCheck;
import com.complyt.domain.Item;
import com.complyt.domain.State;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemQualificationCheckTest {

    ItemQualificationCheck itemQualificationCheck;
    Item item;
    NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        itemQualificationCheck = new ItemQualificationCheck();
        item = createItem();
        nexusStateRule = createNexusStateRule();

    }

    private Item createItem() {
        Item item = new Item(1000, 5, 5000, "description", "item", "C1S1",
                null, null, false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE);
        return item;
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<TaxableCategory>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<TangibleCategory>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<CustomerType>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.CURRENT_CALENDER_YEAR, nexusThreshold);
    }

    @Test
    void isQualified_ItemQualifies_ReturnTrue() {
        // Given

        // When
        boolean isQualified = itemQualificationCheck.isQualified(item, nexusStateRule);

        // Then
        assertTrue(isQualified);
    }

    @Test
    void isQualified_ItemDoesNotQualifyBecauseItsNotTaxable_ReturnFalse() {
        // Given
        Item notTaxableItem = item.withTaxableCategory(TaxableCategory.NOT_TAXABLE);

        // When
        boolean isQualified = itemQualificationCheck.isQualified(notTaxableItem, nexusStateRule);

        // Then
        assertFalse(isQualified);
    }

    @Test
    void isQualified_ItemDoesNotQualifyBecauseItsNotTangible_ReturnFalse() {
        // Given
        Item inTangibleItem = item.withTangibleCategory(TangibleCategory.INTANGIBLE);

        // When
        boolean isQualified = itemQualificationCheck.isQualified(inTangibleItem, nexusStateRule);

        // Then
        assertFalse(isQualified);
    }

    @Test
    void isQualified_NullItemPassed_ThrowsException() {
        // Given
        Item nullItem = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            itemQualificationCheck.isQualified(nullItem, nexusStateRule);
        });

        assertEquals(nullPointerException.getMessage(), "item is marked non-null but is null");
    }

    @Test
    void isQualified_NullNexusStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            itemQualificationCheck.isQualified(item, nullNexusStateRule);
        });

        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }
}
