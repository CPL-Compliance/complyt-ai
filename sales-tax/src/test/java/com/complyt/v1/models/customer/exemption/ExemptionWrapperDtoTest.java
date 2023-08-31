package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.models.StateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExemptionWrapperDtoTest {

    ExemptionWrapperDto exemptionWrapperDto;

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
