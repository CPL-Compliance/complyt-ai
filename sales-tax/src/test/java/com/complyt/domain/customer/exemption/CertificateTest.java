package com.complyt.domain.customer.exemption;

import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class CertificateTest {
    private Certificate certificate;

    private String certificateId;

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