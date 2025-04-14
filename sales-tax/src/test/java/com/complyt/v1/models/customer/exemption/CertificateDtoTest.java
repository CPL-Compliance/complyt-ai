package com.complyt.v1.models.customer.exemption;

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

class CertificateDtoTest {
    private CertificateDto certificateDto;

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
        String expectedString = "CertificateDto[certificateId=" + certificateId +
                ", url=" + certificateDto.url() +
                ", name=" + certificateDto.name() + "]";

        // When
        String actualString = certificateDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}