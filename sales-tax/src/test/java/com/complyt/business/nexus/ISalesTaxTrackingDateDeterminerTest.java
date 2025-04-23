package com.complyt.business.nexus;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;
import static org.mockito.Mockito.when;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ISalesTaxTrackingDateDeterminerTest {
    @InjectMocks
    ISalesTaxTrackingDateDeterminer salesTaxTrackingDateDeterminer;

    @Mock
    ApplicationDateCreator applicationDateCreator;

    SalesTaxTracking salesTaxTracking;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
    }

    @Test
    void getSalesTaxTrackingAppliedDate_EconomicFalsePhysicalFalse_ReturnDefaultDate() {
         LocalDateTime appliedDate = salesTaxTrackingDateDeterminer.getSalesTaxTrackingAppliedDate(salesTaxTracking);
         assertEquals(appliedDate, EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE);
    }

    @Test
    void getSalesTaxTrackingAppliedDate_EconomicTruePhysicalFalse_ReturnEconomicDate() {
        EconomicNexusTracker economicNexusTracker = salesTaxTracking.getEconomicNexusTracker().withEstablished(true).withEstablishedDate(LocalDateTime.now());
        when(applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), economicNexusTracker.getEstablishedDate())).thenReturn(economicNexusTracker.getEstablishedDate());

        LocalDateTime appliedDate = salesTaxTrackingDateDeterminer.getSalesTaxTrackingAppliedDate(salesTaxTracking.withEconomicNexusTracker(economicNexusTracker));
        assertEquals(appliedDate, economicNexusTracker.getEstablishedDate());
    }

    @Test
    void getSalesTaxTrackingAppliedDate_EconomicFalsePhysicalTrue_ReturnPhysicalDate() {
        PhysicalNexusTracker physicalDateTracker = salesTaxTracking.getPhysicalNexusTracker().withEstablished(true).withEstablishedDate(LocalDateTime.now());

        LocalDateTime appliedDate = salesTaxTrackingDateDeterminer.getSalesTaxTrackingAppliedDate(salesTaxTracking.withPhysicalNexusTracker(physicalDateTracker));
        assertEquals(appliedDate, physicalDateTracker.getEstablishedDate());
    }

    @Test
    void getSalesTaxTrackingAppliedDate_EconomicTruePhysicalTrue_EconomicBeforePhysical_ReturnEconomicDate() {
        EconomicNexusTracker economicNexusTracker = salesTaxTracking.getEconomicNexusTracker().withEstablished(true).withEstablishedDate(LocalDateTime.now().minusDays(1));
        PhysicalNexusTracker physicalDateTracker = salesTaxTracking.getPhysicalNexusTracker().withEstablished(true).withEstablishedDate(LocalDateTime.now());

        when(applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), economicNexusTracker.getEstablishedDate())).thenReturn(economicNexusTracker.getEstablishedDate());

        LocalDateTime appliedDate = salesTaxTrackingDateDeterminer.getSalesTaxTrackingAppliedDate(salesTaxTracking.withEconomicNexusTracker(economicNexusTracker).withPhysicalNexusTracker(physicalDateTracker));
        assertEquals(appliedDate, economicNexusTracker.getEstablishedDate());
    }

    @Test
    void getSalesTaxTrackingAppliedDate_EconomicTruePhysicalTrue_PhysicalBeforeEconomic_ReturnPhysicalDate() {
        EconomicNexusTracker economicNexusTracker = salesTaxTracking.getEconomicNexusTracker().withEstablished(true).withEstablishedDate(LocalDateTime.now());
        PhysicalNexusTracker physicalDateTracker = salesTaxTracking.getPhysicalNexusTracker().withEstablished(true).withEstablishedDate(LocalDateTime.now().minusDays(1));

        when(applicationDateCreator.create(salesTaxTracking.getNexusStateRule().timeFrame(), economicNexusTracker.getEstablishedDate())).thenReturn(economicNexusTracker.getEstablishedDate());

        LocalDateTime appliedDate = salesTaxTrackingDateDeterminer.getSalesTaxTrackingAppliedDate(salesTaxTracking.withEconomicNexusTracker(economicNexusTracker).withPhysicalNexusTracker(physicalDateTracker));
        assertEquals(appliedDate, physicalDateTracker.getEstablishedDate());
    }



}