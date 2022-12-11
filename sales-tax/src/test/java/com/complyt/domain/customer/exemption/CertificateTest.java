package com.complyt.domain.customer.exemption;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CertificateTest {
    private Certificate certificate;

    private String certificateId;

    @BeforeEach
    void setup() {
        certificateId = UUID.randomUUID().toString();
        certificate = new Certificate(certificateId, "url", "name");
    }

    @Test
    void Equals_sameCertificate_ReturnsTrue() {
        // Given
        Certificate givenCertificate = new Certificate(certificateId, "url", "name");

        // When
        boolean isEquals = certificate.equals(givenCertificate);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "Certificate(certificateId=" + certificate.getCertificateId() +
                ", url=" + certificate.getUrl() +
                ", name=" + certificate.getName() + ")";

        // When
        String actualString = certificate.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}