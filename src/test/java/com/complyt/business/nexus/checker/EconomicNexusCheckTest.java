package com.complyt.business.nexus.checker;

import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EconomicNexusCheckTest {

    EconomicNexusCheck economicNexusCheck;

    @BeforeEach
    void setUp() {
        economicNexusCheck = new EconomicNexusCheck();
    }

    @Test
    void check_CheckingNexusTracker_ReturnsIsEstablished() {
        // Given
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(true,new Date());
        State state = new State("CA","02","California");
        SalesTaxTracking salesTaxTracking = new SalesTaxTracking(UUID.randomUUID().toString(),state,new ObjectId(),
                true,null,economicNexusTracker);

        // When + Then
        boolean hasEconomicNexus = economicNexusCheck.check(salesTaxTracking);
        assertTrue(hasEconomicNexus);

    }
}
