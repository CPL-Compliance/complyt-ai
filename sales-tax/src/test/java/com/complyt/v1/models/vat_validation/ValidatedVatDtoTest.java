package com.complyt.v1.models.vat_validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ValidatedVatDtoTest {

    ValidatedVatDto validatedVatDto;

    UnitTestUtilities testUtilities;


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
