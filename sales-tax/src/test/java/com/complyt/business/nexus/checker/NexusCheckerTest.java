package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.utils.factory.DateRange;
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
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
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

    DateRange dateRange;
    NexusCalculationSummary nexusCalculationSummary;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        dateRange = DateRange.Factory.newCurrentCalenderYear(LocalDateTime.now());
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString());
        nexusCalculationSummary = createNexusCalculationSummary();
        salesTaxTracking.getNexusCalculationSummaries().put(dateRange.getEnd().toLocalDate(), nexusCalculationSummary);
    }

    private NexusCalculationSummary createNexusCalculationSummary() {
        return new NexusCalculationSummary(10, new BigDecimal(10000));
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
    void passedThreshold_NoNexusCalculationSummaryAtDateRange_ReturnsFalse() {
        // When
        boolean passedThreshold = nexusChecker.passedThreshold(salesTaxTracking, DateRange.Factory.newPreviousTwelveMonths(LocalDateTime.now()));

        // Then
        Assertions.assertFalse(passedThreshold);
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
        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(nexusCalculationSummary, salesTaxTracking.getNexusStateRule());

        // When
        when(nexusThresholdChecker.check(summaryAndRule)).thenReturn(true);
        boolean passedThreshold = nexusChecker.passedThreshold(salesTaxTracking, dateRange);

        // Then
        assertTrue(passedThreshold);
    }

    @Test
    void passedThreshold_SummaryDoesNotPassedThreshold_ReturnsFalse() {
        // Given
        Pair<NexusCalculationSummary, NexusStateRule> summaryAndRule = new Pair<>(nexusCalculationSummary, salesTaxTracking.getNexusStateRule());

        // When
        when(nexusThresholdChecker.check(summaryAndRule)).thenReturn(false);
        boolean passedThreshold = nexusChecker.passedThreshold(salesTaxTracking, dateRange);

        // Then
        assertFalse(passedThreshold);
    }

    @Test
    void hasNexus_NullSummaryPassed_ThrowsException() {
        // Given
        SalesTaxTracking salesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusChecker.passedThreshold(salesTaxTracking, dateRange);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void passedThreshold_NullStateRulePassed_ThrowsException() {
        // Given
        DateRange dateRange = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusChecker.passedThreshold(salesTaxTracking, dateRange);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "dateRange is marked non-null but is null");
    }

}