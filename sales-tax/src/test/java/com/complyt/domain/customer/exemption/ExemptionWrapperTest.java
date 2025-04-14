package com.complyt.domain.customer.exemption;

import com.complyt.domain.State;
import com.complyt.security.TenantResolver;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

public class ExemptionWrapperTest {

    ExemptionWrapper exemptionWrapper;

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