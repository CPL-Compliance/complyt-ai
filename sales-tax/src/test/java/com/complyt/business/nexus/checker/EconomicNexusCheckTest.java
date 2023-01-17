package com.complyt.business.nexus.checker;

import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class EconomicNexusCheckTest {

    EconomicNexusChecker economicNexusChecker;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        economicNexusChecker = new EconomicNexusChecker();
    }

    @Test
    void check_CheckingNexusTracker_ReturnsIsEstablished() {
        // Given
        SalesTaxTracking salesTaxTracking =  domainObjectStub.createSalesTaxTracking(new ObjectId().toString())
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));

        // When + Then
        boolean hasEconomicNexus = economicNexusChecker.check(salesTaxTracking);
        assertTrue(hasEconomicNexus);

    }

    @Test
    void check_NullTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        //When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            economicNexusChecker.check(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }
}
