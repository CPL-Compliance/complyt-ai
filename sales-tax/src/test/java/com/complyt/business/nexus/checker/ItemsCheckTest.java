package com.complyt.business.nexus.checker;

import com.complyt.domain.Item;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemsCheckTest {

    private ItemStateThresholdQualifier itemStateThresholdQualifier;

    private List<Item> items;
    private NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        itemStateThresholdQualifier = new ItemStateThresholdQualifier();
        items = createItems();
        nexusStateRule = createNexusStateRule();
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, null, null, null);
    }

    private List<Item> createItems() {
        Item item = new Item(10, 5, 50, "description", "name", "C1S1", null,
                null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);
        return new ArrayList<>() {{
            add(item);
        }};
    }

    @Test
    void check_NoItemsThatCountsRegardingToNexusRule_ReturnsFalse() {
        // Given
        Pair<List<Item>, NexusStateRule> nexusStateRulePair = new Pair<>(items, nexusStateRule);

        // When + Then
        boolean doItemsCount = itemStateThresholdQualifier.check(nexusStateRulePair);
        assertFalse(doItemsCount);
    }

    @Test
    void check_NoItemsThatCountsRegardingToNexusRule_ReturnsTrue() {
        // Given
        TangibleCategory tangibleCategory = nexusStateRule.getTangibleCategories().get(0);
        TaxableCategory taxableCategory = nexusStateRule.getTaxableCategories().get(0);
        Item itemThatCounts = items.get(0).withTangibleCategory(tangibleCategory).withTaxableCategory(taxableCategory);
        items.add(itemThatCounts);

        Pair<List<Item>, NexusStateRule> nexusStateRulePair = new Pair<>(items, nexusStateRule);

        // When + Then
        boolean doItemsCount = itemStateThresholdQualifier.check(nexusStateRulePair);
        assertTrue(doItemsCount);
    }

    @Test
    void check_NullPairPassed_ThrowsException() {
        // Given
        Pair<List<Item>, NexusStateRule> nexusStateRulePair = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> itemStateThresholdQualifier.check(nexusStateRulePair));

        assertEquals(nullPointerException.getMessage(), "itemsAndRule is marked non-null but is null");
    }
}
