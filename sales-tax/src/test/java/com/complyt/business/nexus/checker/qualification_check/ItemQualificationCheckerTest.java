package com.complyt.business.nexus.checker.qualification_check;

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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class ItemQualificationCheckerTest {

    QualificationChecker qualificationChecker;
    Item item;
    NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        qualificationChecker = new QualificationChecker();
        item = createItem();
        nexusStateRule = createNexusStateRule();

    }

    private Item createItem() {
        return new Item(1000, 5, 5000, "description", "item", "C1S1",
                null, null, false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE);
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<>() {{
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
        boolean isQualified = qualificationChecker.isQualified(item, nexusStateRule);

        // Then
        assertTrue(isQualified);
    }

    @Test
    void isQualified_ItemDoesNotQualifyBecauseItsNotTaxable_ReturnFalse() {
        // Given
        Item notTaxableItem = item.withTaxableCategory(TaxableCategory.NOT_TAXABLE);

        // When
        boolean isQualified = qualificationChecker.isQualified(notTaxableItem, nexusStateRule);

        // Then
        assertFalse(isQualified);
    }

    @Test
    void isQualified_ItemDoesNotQualifyBecauseItsNotTangible_ReturnFalse() {
        // Given
        Item inTangibleItem = item.withTangibleCategory(TangibleCategory.INTANGIBLE);

        // When
        boolean isQualified = qualificationChecker.isQualified(inTangibleItem, nexusStateRule);

        // Then
        assertFalse(isQualified);
    }

    @Test
    void isQualified_NullNexusStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> qualificationChecker.isQualified(item, nullNexusStateRule));

        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }
}
