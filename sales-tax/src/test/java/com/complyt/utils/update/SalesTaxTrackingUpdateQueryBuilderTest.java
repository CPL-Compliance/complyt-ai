package com.complyt.utils.update;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TimeFrame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Update;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SalesTaxTrackingUpdateQueryBuilderTest {

    UnitTestUtilities testUtilities;

    SalesTaxTracking salesTaxTracking;

    SalesTaxTrackingUpdateQueryBuilder updateQueryBuilder;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
        updateQueryBuilder = new SalesTaxTrackingUpdateQueryBuilder();
    }

    @Test
    void build_SalesTaxTrackingWithTimeFrameOfPreviousTwelveMonths_ReturnsUpdate() {
        // Given + When
        Update expectedUpdate = testUtilities.buildSalesTaxTrackingUpdateOfPreviousTwelveMonths(salesTaxTracking);
        Update actualUpdate = updateQueryBuilder.build(salesTaxTracking);

        // Then
        Assertions.assertEquals(expectedUpdate, actualUpdate);
    }

    @Test
    void build_SalesTaxTrackingWithDifferentTimeFrameThanPreviousTwelveMonths_ReturnsUpdate() {
        // Given + When
        Update expectedUpdate = testUtilities.buildSalesTaxTrackingUpdate(salesTaxTracking);
        NexusStateRule nexusStateRule = testUtilities.createNexusStateRule(UUID.randomUUID().toString())
                .withTimeFrame(TimeFrame.PREVIOUS_CALENDER_YEAR);
        SalesTaxTracking salesTaxTrackingWithPreviousTwelveMonthTimeFrame = salesTaxTracking.withNexusStateRule(nexusStateRule);

        Update actualUpdate = updateQueryBuilder.build(salesTaxTrackingWithPreviousTwelveMonthTimeFrame);

        // Then
        Assertions.assertEquals(expectedUpdate, actualUpdate);
    }

    @Test
    void build_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given + When
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            updateQueryBuilder.build(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

}
