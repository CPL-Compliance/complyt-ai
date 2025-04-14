package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.security.TenantResolver;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class EconomicNexusCheckTest {

    EconomicNexusChecker economicNexusChecker;
    UnitTestUtilities testUtilities;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        economicNexusChecker = new EconomicNexusChecker();
    }

    @Test
    void check_CheckingNexusTracker_ReturnsIsEstablished() {
        // Given
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString())
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
