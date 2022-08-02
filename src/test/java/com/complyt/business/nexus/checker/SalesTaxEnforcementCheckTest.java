package com.complyt.business.nexus.checker;

import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.*;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxEnforcementCheckTest {

    SalesTaxEnforcementCheck salesTaxEnforcementCheck;

    @BeforeEach
    void setUp() {
        salesTaxEnforcementCheck = new SalesTaxEnforcementCheck();
    }

    @Test
    void check_CheckingSalesTaxTracking_ReturnsIsEnforcesSalesTax() {
        // Given
        State state = new State("CA","02","California");
        SalesTaxTracking salesTaxTracking = new SalesTaxTracking(UUID.randomUUID().toString(),
                state,new ObjectId(),true,null,null,null);

        // When + Then
        boolean isEnforcesSalesTax = salesTaxEnforcementCheck.check(salesTaxTracking);
        assertTrue(isEnforcesSalesTax);
    }

    @Test
    void check_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        //When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxEnforcementCheck.check(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

}
