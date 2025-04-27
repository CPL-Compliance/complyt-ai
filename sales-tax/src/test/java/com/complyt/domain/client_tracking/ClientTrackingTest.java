package com.complyt.domain.client_tracking;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;

class ClientTrackingTest {

    String id;
    String tenantId;
    private ClientTracking clientTracking;
    private LocalDateTime nexusDate;

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
        id = UUID.randomUUID().toString();
        tenantId = UUID.randomUUID().toString();
        nexusDate = LocalDateTime.now();
        clientTracking = new ClientTracking(id, tenantId, new Nexus(nexusDate), "name", null, null,
                false, "", "");
    }

    @Test
    void Equals_sameClientTracking_ReturnsTrue() {
        // Given
        ClientTracking givenClientTracking = new ClientTracking(id, tenantId, new Nexus(nexusDate), "name", null, null, false, "", "");

        // When
        boolean isEquals = clientTracking.equals(givenClientTracking);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ClientTracking(id=" + clientTracking.getId() +
                ", tenantId=" + clientTracking.getTenantId() +
                ", nexus=" + clientTracking.getNexus() +
                ", name=" + clientTracking.getName() +
                ", internalTimestamps=" + clientTracking.getInternalTimestamps() +
                ", subsidiaries=" + clientTracking.getSubsidiaries() +
                ", shouldForwardWriteOperations=" + clientTracking.getShouldForwardWriteOperations() +
                ", host=" + clientTracking.getHost() +
                ", path=" + clientTracking.getPath() +
                ")";

        // When
        String actualString = clientTracking.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}