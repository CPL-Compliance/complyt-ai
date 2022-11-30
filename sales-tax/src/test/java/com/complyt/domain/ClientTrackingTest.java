package com.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientTrackingTest {

    private ClientTracking clientTracking;
    private LocalDateTime nexusDate;
    String id;
    String tenantId;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        tenantId = UUID.randomUUID().toString();
        nexusDate = LocalDateTime.now();
        clientTracking = new ClientTracking(id, tenantId, new Nexus(nexusDate));
    }

    @Test
    void Equals_sameClientTracking_ReturnsTrue() {
        // Given
        ClientTracking givenClientTracking = new ClientTracking(id, tenantId, new Nexus(nexusDate));

        // When
        boolean expectedBoolean = clientTracking.equals(givenClientTracking);

        // Then
        assertTrue(expectedBoolean);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ClientTracking(id=" + id + ", tenantId=" + tenantId + ", nexus=Nexus(taxableDate=" + nexusDate + "))";

        // When
        String actualString = clientTracking.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}