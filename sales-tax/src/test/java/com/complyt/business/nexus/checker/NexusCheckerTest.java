package com.complyt.business.nexus.checker;

import com.complyt.domain.State;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.Definition;
import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.ut.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusCheckerTest {

    @InjectMocks
    NexusChecker nexusChecker;

    @Mock
    PhysicalNexusChecker physicalNexusChecker;

    @Mock
    EconomicNexusChecker economicNexusChecker;

    @Mock
    SalesTaxEnforcementChecker salesTaxEnforcementChecker;

    @Mock
    NexusThresholdChecker nexusThresholdChecker;

    SalesTaxTracking salesTaxTracking;
    NexusCalculationSummary nexusCalculationSummary;
    NexusStateRule nexusStateRule;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString());
        nexusCalculationSummary = createNexusCalculationSummary();
        nexusStateRule = createNexusStateRule();
    }

    private NexusStateRule createNexusStateRule() {
        NexusThreshold nexusThreshold = new NexusThreshold(10, 10000, Definition.AMOUNT_OR_COUNT);
        State state = new State("CA", "02", "California");
        return new NexusStateRule(UUID.randomUUID().toString(), true, state, null, null, null,
                null, nexusThreshold);
    }

    private NexusCalculationSummary createNexusCalculationSummary() {
        return new NexusCalculationSummary(10, 10000);
    }

    @Test
    void hasNexus_TrackingIndicatesThatClientHasPhysicalNexus_ReturnsTrue() {
        // Given

        // When
        when(salesTaxEnforcementChecker.check(salesTaxTracking)).thenReturn(true);
        when(physicalNexusChecker.check(salesTaxTracking)).thenReturn(true);
        boolean hasNexus = nexusChecker.hasNexus(salesTaxTracking);

        // Then
        Assertions.assertTrue(hasNexus);
    }

    @Test
    void hasNexus_TrackingIndicatesThatClientHasEconomicNexus_ReturnsTrue() {
        // Given

        // When
        when(salesTaxEnforcementChecker.check(salesTaxTracking)).thenReturn(true);
        when(physicalNexusChecker.check(salesTaxTracking)).thenReturn(false);
        when(economicNexusChecker.check(salesTaxTracking)).thenReturn(true);
        boolean hasNexus = nexusChecker.hasNexus(salesTaxTracking);

        // Then
        Assertions.assertTrue(hasNexus);
    }

    @Test
    void hasNexus_TrackingIndicatesThatClientDoesNotHaveNexus_ReturnsFalse() {
        // Given

        // When
        when(salesTaxEnforcementChecker.check(salesTaxTracking)).thenReturn(true);
        when(physicalNexusChecker.check(salesTaxTracking)).thenReturn(false);
        when(economicNexusChecker.check(salesTaxTracking)).thenReturn(false);
        boolean hasNexus = nexusChecker.hasNexus(salesTaxTracking);

        // Then
        Assertions.assertFalse(hasNexus);
    }

    @Test
    void hasNexus_TrackingIndicatesThatStateDoesntChargeSalesTax_ReturnsFalse() {
        // Given

        // When
        when(salesTaxEnforcementChecker.check(salesTaxTracking)).thenReturn(false);
        boolean hasNexus = nexusChecker.hasNexus(salesTaxTracking);

        // Then
        Assertions.assertFalse(hasNexus);
    }

    @Test
    void hasNexus_NullTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusChecker.hasNexus(nullSalesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void passedThreshold_SummaryPassedThreshold_ReturnsTrue() {
        // Given
        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(nexusCalculationSummary, nexusStateRule);

        // When
        when(nexusThresholdChecker.check(summaryAndRule)).thenReturn(true);
        boolean passedThreshold = nexusChecker.passedThreshold(nexusCalculationSummary, nexusStateRule);

        // Then
        assertTrue(passedThreshold);
    }

    @Test
    void passedThreshold_SummaryDoesNotPassedThreshold_ReturnsFalse() {
        // Given
        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(nexusCalculationSummary, nexusStateRule);

        // When
        when(nexusThresholdChecker.check(summaryAndRule)).thenReturn(false);
        boolean passedThreshold = nexusChecker.passedThreshold(nexusCalculationSummary, nexusStateRule);

        // Then
        assertFalse(passedThreshold);
    }

    @Test
    void hasNexus_NullSummaryPassed_ThrowsException() {
        // Given
        NexusCalculationSummary nullCalculationSummary = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusChecker.passedThreshold(nullCalculationSummary, nexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "calculationSummary is marked non-null but is null");
    }

    @Test
    void passedThreshold_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusChecker.passedThreshold(nexusCalculationSummary, nullStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "stateRule is marked non-null but is null");
    }

}