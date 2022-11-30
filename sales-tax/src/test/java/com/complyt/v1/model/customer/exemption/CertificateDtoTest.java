package com.complyt.v1.model.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateDtoTest {
    private CertificateDto certificateDto;

    private String certificateId;

    @BeforeEach
    void setup() {
        certificateId = UUID.randomUUID().toString();
        certificateDto = new CertificateDto(certificateId, "url", "name");
    }

    @Test
    void Equals_sameCertificateDto_ReturnsTrue() {
        // Given
        CertificateDto givenCertificateDto = new CertificateDto(certificateId, "url", "name");

        // When
        boolean expectedBoolean = certificateDto.equals(givenCertificateDto);

        // Then
        assertTrue(expectedBoolean);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "CertificateDto(certificateId=" + certificateId + ", url=url, name=name)";

        // When
        String actualString = certificateDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}