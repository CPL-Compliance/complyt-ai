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
        boolean isEquals = certificateDto.equals(givenCertificateDto);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "CertificateDto(certificateId=" + certificateId +
                ", url=" + certificateDto.getUrl() +
                ", name=" + certificateDto.getName() + ")";

        // When
        String actualString = certificateDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}