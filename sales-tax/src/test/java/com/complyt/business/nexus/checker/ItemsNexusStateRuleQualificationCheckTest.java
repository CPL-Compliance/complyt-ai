package com.complyt.business.nexus.checker;

import com.complyt.business.nexus.checker.qualification_check.ItemQualificationCheck;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemsNexusStateRuleQualificationCheckTest {

    @InjectMocks
    ItemsNexusStateRuleQualificationCheck itemsNexusStateRuleQualificationCheck;

    @Mock
    ItemQualificationCheck itemQualificationCheck;

    private List<Item> items;
    private NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        items = createItems();
        nexusStateRule = createNexusStateRule();
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<TaxableCategory>() {{
            add(TaxableCategory.TAXABLE);
        }};
        List<TangibleCategory> tangibleCategories = new ArrayList<TangibleCategory>() {{
            add(TangibleCategory.TANGIBLE);
        }};
        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, null, null, null);
    }

    private List<Item> createItems() {
        Item item = new Item(10, 5, 50, "description", "name", "C1S1", null,
                null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);
        return new ArrayList<Item>() {{
            add(item);
        }};
    }

    @Test
    void check_NoItemsThatCountsRegardingToNexusRule_ReturnsFalse() {
        // Given
        Pair<List<Item>, NexusStateRule> nexusStateRulePair = new Pair(items, nexusStateRule);

        // When + Then
        boolean doItemsCount = itemsNexusStateRuleQualificationCheck.check(nexusStateRulePair);
        assertFalse(doItemsCount);
    }

    @Test
    void check_NoItemsThatCountsRegardingToNexusRule_ReturnsTrue() {
        // Given
        TangibleCategory tangibleCategory = nexusStateRule.getTangibleCategories().get(0);
        TaxableCategory taxableCategory = nexusStateRule.getTaxableCategories().get(0);
        Item itemThatCounts = items.get(0).withTangibleCategory(tangibleCategory).withTaxableCategory(taxableCategory);
        items.add(itemThatCounts);

        when(itemQualificationCheck.isQualified(items.get(0), nexusStateRule)).thenReturn(true);

        Pair<List<Item>, NexusStateRule> nexusStateRulePair = new Pair(items, nexusStateRule);

        // When + Then
        boolean doItemsCount = itemsNexusStateRuleQualificationCheck.check(nexusStateRulePair);
        assertTrue(doItemsCount);
    }

    @Test
    void check_NullPairPassed_ThrowsException() {
        // Given
        Pair<List<Item>, NexusStateRule> nexusStateRulePair = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            itemsNexusStateRuleQualificationCheck.check(nexusStateRulePair);
        });

        assertEquals(nullPointerException.getMessage(), "itemsAndRule is marked non-null but is null");
    }
}
