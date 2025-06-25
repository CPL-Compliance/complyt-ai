package io.complyt.domain.client_tracking;

import io.complyt.domain.ClientTracking;
import io.complyt.domain.Nexus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClientTrackingTest {

    String id;
    String tenantId;
    private ClientTracking clientTracking;
    private LocalDateTime nexusDate;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        tenantId = UUID.randomUUID().toString();
        nexusDate = LocalDateTime.now();
        clientTracking = new ClientTracking(id, tenantId, new Nexus(nexusDate), "name", null, null, null);
    }

    @Test
    void Equals_sameClientTracking_ReturnsTrue() {
        // Given
        ClientTracking givenClientTracking = new ClientTracking(id, tenantId, new Nexus(nexusDate), "name", null, null, null);

        // When
        boolean isEquals = clientTracking.equals(givenClientTracking);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void toString_ReturnsString() {
        // Given
        String expectedString = "ClientTracking[id=" + clientTracking.id() +
                ", tenantId=" + clientTracking.tenantId() +
                ", nexus=" + clientTracking.nexus() +
                ", name=" + clientTracking.name() +
                ", internalTimestamps=" + clientTracking.internalTimestamps() +
                ", subsidiaries=" + clientTracking.subsidiaries() +
                ", webhookDetails=" + clientTracking.webhookDetails() +
                "]";

        // When
        String actualString = clientTracking.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
