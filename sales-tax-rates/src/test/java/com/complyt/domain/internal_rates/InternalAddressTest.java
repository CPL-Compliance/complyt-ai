package com.complyt.domain.internal_rates;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InternalAddressTest {
    InternalAddress internalAddress;

    @BeforeEach
    void setup() {
        internalAddress = TestUtilities.createInternalAddress();
    }

    @Test
    void Equals_SameInternalRates_ReturnsTrue() {
        // Given
        InternalAddress givenInternalAddress = new InternalAddress("California", "Fresno", "Fresno", false, "", 0,0);

        // When + Then
        boolean isEquals = internalAddress.equals(givenInternalAddress);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "InternalAddress[" +
                "state=California, " +
                "county=Fresno, " +
                "city=Fresno, " +
                "isUnincorporated=false, " +
                "zip=, " +
                "lowerPlusFourDigits=0, " +
                "upperPlusFourDigits=0]";

        // When
        String actualString = internalAddress.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
