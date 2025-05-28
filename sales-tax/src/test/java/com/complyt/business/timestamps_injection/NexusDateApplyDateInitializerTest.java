package com.complyt.business.timestamps_injection;

import static org.junit.jupiter.api.Assertions.*;

import com.complyt.business.nexus.SalesTaxTrackingDateDeterminer;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class NexusDateApplyDateInitializerTest {

    @Mock
    SalesTaxTrackingDateDeterminer dateDeterminer;

    @InjectMocks
    NexusDateApplyDateInitializer dateInitializer;

    SalesTaxTracking salesTaxTracking;
    UnitTestUtilities testUtilities;


    @Test
    void init_setsAppliedDateCorrectly() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
        LocalDateTime now = LocalDateTime.now();

        when(dateDeterminer.getSalesTaxTrackingAppliedDate(any())).thenReturn(now);

        salesTaxTracking.setPhysicalNexusTracker(
                salesTaxTracking.getPhysicalNexusTracker().withEstablishedDate(now)
        );
        SalesTaxTracking updated = dateInitializer.init(salesTaxTracking);

        assertEquals(salesTaxTracking.getPhysicalNexusTracker().getEstablishedDate(), updated.getPhysicalNexusTracker().getEstablishedDate());
        assertEquals(salesTaxTracking.getAppliedDate(), updated.getAppliedDate());
    }

    @Test
    void init_setsAppliedDate_NullSalesTaxTracking_ReturnsNullException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dateInitializer.init(null));

        assertEquals("salesTaxTracking is marked non-null but is null", exception.getMessage());
    }
}
