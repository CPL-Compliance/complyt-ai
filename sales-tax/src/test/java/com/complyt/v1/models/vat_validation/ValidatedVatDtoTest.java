package com.complyt.v1.models.vat_validation;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

public class ValidatedVatDtoTest {

    ValidatedVatDto validatedVatDto;

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
        validatedVatDto = testUtilities.createValidatedVatDto();
    }

    @Test
    void equals_SameFields_ReturnTrue() {
        ValidatedVatDto another = testUtilities.createValidatedVatDto().withInternalTimestamps(validatedVatDto.internalTimestamps());
        assertTrue(validatedVatDto.equals(another));
    }
}
