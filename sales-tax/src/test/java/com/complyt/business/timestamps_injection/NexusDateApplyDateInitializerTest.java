package com.complyt.business.timestamps_injection;

import static org.junit.jupiter.api.Assertions.*;

import com.complyt.business.nexus.ISalesTaxTrackingDateDeterminer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;


@ExtendWith(MockitoExtension.class)
class NexusDateApplyDateInitializerTest {

    @Mock
    ISalesTaxTrackingDateDeterminer dateDeterminer;

    @InjectMocks
    NexusDateApplyDateInitializer dateInitializer;


    @Test
    void init_setsAppliedDate_NullSalesTaxTracking_ReturnsNullException() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> dateInitializer.init(null));

        assertEquals("salesTaxTracking is marked non-null but is null", exception.getMessage());
    }
}
