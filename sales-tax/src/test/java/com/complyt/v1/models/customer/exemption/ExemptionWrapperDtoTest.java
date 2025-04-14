package com.complyt.v1.models.customer.exemption;

import com.complyt.security.TenantResolver;
import com.complyt.v1.models.StateDto;
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

public class ExemptionWrapperDtoTest {

    ExemptionWrapperDto exemptionWrapperDto;

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
        List<StateDto> states = UnitTestUtilities.createStateListDto();
        ExemptionDto exemptionDto = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString()).createExemptionDto();
        exemptionWrapperDto = new ExemptionWrapperDto(exemptionDto, states);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ExemptionWrapperDto[exemption=" + exemptionWrapperDto.exemption() +
                ", states=" + exemptionWrapperDto.states() + "]";

        // When
        String actualString = exemptionWrapperDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
