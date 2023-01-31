package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PhysicalNexusCheckTest {

    PhysicalNexusChecker physicalNexusChecker;
    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        physicalNexusChecker = new PhysicalNexusChecker();
    }

    @Test
    void check_CheckingNexusTracker_ReturnsIsEstablished() {
        // Given
        SalesTaxTracking salesTaxTracking = objectStub.createSalesTaxTracking(new ObjectId().toString())
                .withPhysicalNexusTracker(new PhysicalNexusTracker(true, LocalDateTime.now()));

        // When + Then
        boolean hasPhysicalNexus = physicalNexusChecker.check(salesTaxTracking);
        assertTrue(hasPhysicalNexus);
    }

    @Test
    void check_NullTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        //When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            physicalNexusChecker.check(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

}
