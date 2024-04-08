package com.complyt.business.nexus.checker.qualification_check;

import com.complyt.domain.State;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
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

public class ItemQualificationCheckerTest {

    private UnitTestUtilities testUtilities;
    private QualificationChecker qualificationChecker;
    private Item item;
    private NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        qualificationChecker = new QualificationChecker();
        item = testUtilities.createItems(false, false, true).get(0).withTaxCode("C1S1");
        nexusStateRule = createNexusStateRule();

    }

    private NexusStateRule createNexusStateRule() {
        String country = "USA";
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

        NexusThreshold nexusThreshold = new NexusThreshold(new BigDecimal(1000), 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, country, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.CURRENT_CALENDER_YEAR, nexusThreshold, LocalDateTime.now());
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
