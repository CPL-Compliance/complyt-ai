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
        boolean expectedBoolean = certificate.equals(givenCertificate);

        // Then
        assertTrue(expectedBoolean);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Certificate(certificateId=" + certificateId + ", url=url, name=name)";

        // When
        String actualString = certificate.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}