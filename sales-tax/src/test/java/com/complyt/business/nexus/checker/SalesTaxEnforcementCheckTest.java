package com.complyt.business.nexus.checker;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxEnforcementCheckTest {

    SalesTaxEnforcementChecker salesTaxEnforcementChecker;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxEnforcementChecker = new SalesTaxEnforcementChecker();
    }

    @Test
    void check_CheckingSalesTaxTracking_ReturnsIsEnforcesSalesTax() {
        // Given
        SalesTaxTracking salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString());

        // When + Then
        boolean isEnforcesSalesTax = salesTaxEnforcementChecker.check(salesTaxTracking);
        assertTrue(isEnforcesSalesTax);
    }

    @Test
    void check_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        //When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxEnforcementChecker.check(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

}
