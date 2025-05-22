package com.complyt.v1.models.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class StatusDtoTest {

    private StatusDto statusDto;

   

    @BeforeEach
    void setup() {
        statusDto = new StatusDto("code", "name");
    }

    @Test
    void Equals_sameStatusDto_ReturnTrue() {
        // Given
        StatusDto givenStatusDto = new StatusDto("code", "name");

        // When
        boolean isEquals = statusDto.equals(givenStatusDto);

        // Then
        assertTrue(isEquals);
    }
}