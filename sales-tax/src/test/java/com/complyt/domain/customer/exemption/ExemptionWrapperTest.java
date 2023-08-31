package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExemptionWrapperTest {

    ExemptionWrapper exemptionWrapper;

    @BeforeEach
    void setup() {
        UnitTestUtilities testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        List<State> states = UnitTestUtilities.createStateList();
        Exemption exemption = testUtilities.createExemption(new ObjectId().toString());
        exemptionWrapper = new ExemptionWrapper(exemption, states);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ExemptionWrapper[exemption=" + exemptionWrapper.exemption() +
                ", states=" + exemptionWrapper.states() + "]";

        // When
        String actualString = exemptionWrapper.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}