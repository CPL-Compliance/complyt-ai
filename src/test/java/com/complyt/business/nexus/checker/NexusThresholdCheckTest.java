package com.complyt.business.nexus.checker;

import com.complyt.domain.CustomerType;
import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
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
public class NexusThresholdCheckTest {

    NexusThresholdCheck nexusThresholdCheck;
    NexusStateRule nexusStateRule;
    NexusCalculationSummary nexusCalculationSummary;

    @BeforeEach
    void setUp() {
        nexusThresholdCheck = new NexusThresholdCheck();
        nexusStateRule = createNexusStateRule();
        nexusCalculationSummary = createNexusCalculationSummary();
    }

    private NexusCalculationSummary createNexusCalculationSummary() {
        return new NexusCalculationSummary(nexusStateRule.getNexusThreshold().getCount(),
                nexusStateRule.getNexusThreshold().getAmount());
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
                TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR, nexusThreshold);
    }

    @Test
    void check_NullPairPassed_ThrowsException() {
        // Given
        Pair<NexusCalculationSummary, NexusStateRule> nullSummaryAndRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusThresholdCheck.check(nullSummaryAndRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "summaryAndRule is marked non-null but is null");
    }

    @Test
    void check_DefinitionOfAmount_ReturnsTrue() {
        // Given
        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT);
        NexusStateRule nexusStateRuleWithAmountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(nexusCalculationSummary,nexusStateRuleWithAmountDefinition);

        // When
        boolean exceededAmount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertTrue(exceededAmount);
    }

    @Test
    void check_DefinitionOfAmount_ReturnsFalse() {
        // Given
        float lowerAmountThanRule = nexusStateRule.getNexusThreshold().getAmount()-1;
        NexusCalculationSummary summary = nexusCalculationSummary.withAmount(lowerAmountThanRule);

        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT);
        NexusStateRule nexusStateRuleWithAmountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithAmountDefinition);

        // When
        boolean exceededAmount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertFalse(exceededAmount);
    }

    @Test
    void check_DefinitionOfCount_ReturnsTrue() {
        // Given
        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.COUNT);
        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(nexusCalculationSummary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertTrue(exceededCount);
    }

    @Test
    void check_DefinitionOfCount_ReturnsFalse() {
        // Given
        int lowerCountThanRule = nexusStateRule.getNexusThreshold().getCount()-1;
        NexusCalculationSummary summary = nexusCalculationSummary.withCount(lowerCountThanRule);
        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.COUNT);
        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertFalse(exceededCount);
    }

    @Test
    void check_DefinitionOfAmountAndCount_ReturnsTrue() {
        // Given
        NexusCalculationSummary summary = nexusCalculationSummary
                .withCount(nexusStateRule.getNexusThreshold().getCount())
                .withAmount(nexusStateRule.getNexusThreshold().getAmount());
        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT_AND_COUNT);

        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededAmountAndCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertTrue(exceededAmountAndCount);
    }

    @Test
    void check_DefinitionOfAmountAndCount_ReturnsFalseBecauseOfCount() {
        // Given
        NexusCalculationSummary summary = nexusCalculationSummary
                .withCount(nexusStateRule.getNexusThreshold().getCount()-1);
        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT_AND_COUNT);

        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededAmountAndCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertFalse(exceededAmountAndCount);
    }

    @Test
    void check_DefinitionOfAmountAndCount_ReturnsFalseBecauseOfAmount() {
        // Given
        NexusCalculationSummary summary = nexusCalculationSummary
                .withAmount(nexusStateRule.getNexusThreshold().getAmount()-1);
        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT_AND_COUNT);

        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededAmountAndCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertFalse(exceededAmountAndCount);
    }

    @Test
    void check_DefinitionOfAmountOrCount_ReturnsTrueBecauseOfAmount() {
        // Given
        NexusCalculationSummary summary = nexusCalculationSummary
                .withAmount(nexusStateRule.getNexusThreshold().getAmount())
                .withCount(nexusStateRule.getNexusThreshold().getCount()-1);

        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT_OR_COUNT);

        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededAmountOrCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertTrue(exceededAmountOrCount);
    }

    @Test
    void check_DefinitionOfAmountOrCount_ReturnsTrueBecauseOfCount() {
        // Given
        NexusCalculationSummary summary = nexusCalculationSummary
                .withAmount(nexusStateRule.getNexusThreshold().getAmount()-1)
                .withCount(nexusStateRule.getNexusThreshold().getCount());

        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT_OR_COUNT);

        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededAmountOrCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertTrue(exceededAmountOrCount);
    }

    @Test
    void check_DefinitionOfAmountOrCount_ReturnsTrueBecauseOfCountAndAmount() {
        // Given
        NexusCalculationSummary summary = nexusCalculationSummary
                .withAmount(nexusStateRule.getNexusThreshold().getAmount())
                .withCount(nexusStateRule.getNexusThreshold().getCount());

        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT_OR_COUNT);

        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededAmountOrCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertTrue(exceededAmountOrCount);
    }

    @Test
    void check_DefinitionOfAmountOrCount_ReturnsFalse() {
        // Given
        NexusCalculationSummary summary = nexusCalculationSummary
                .withAmount(nexusStateRule.getNexusThreshold().getAmount()-1)
                .withCount(nexusStateRule.getNexusThreshold().getCount()-1);

        NexusThreshold nexusThreshold = new NexusThreshold(nexusStateRule.getNexusThreshold().getAmount(),
                nexusStateRule.getNexusThreshold().getCount(),Definition.AMOUNT_OR_COUNT);

        NexusStateRule nexusStateRuleWithCountDefinition = nexusStateRule.withNexusThreshold(nexusThreshold);

        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(summary,nexusStateRuleWithCountDefinition);

        // When
        boolean exceededAmountOrCount = nexusThresholdCheck.check(summaryAndRule);

        // Then
        assertFalse(exceededAmountOrCount);
    }
}
