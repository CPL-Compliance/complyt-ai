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

public class VatDetailsToValidateDtoTest {

    VatDetailsToValidateDto vatDetailsToValidateDto;

    UnitTestUtilities testUtilities;


   

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        vatDetailsToValidateDto = testUtilities.createVatDetailsToValidateDto();
    }

    @Test
    void equals_SameFields_ReturnTrue() {
        VatDetailsToValidateDto another = testUtilities.createVatDetailsToValidateDto();

        assertTrue(vatDetailsToValidateDto.equals(another));
    }
}
