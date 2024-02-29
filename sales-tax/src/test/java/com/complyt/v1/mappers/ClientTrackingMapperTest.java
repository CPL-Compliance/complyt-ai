package com.complyt.v1.mappers;

import com.complyt.domain.ClientTracking;
import com.complyt.v1.models.ClientTrackingDtoTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ClientTrackingMapperTest {

   private ClientTracking clientTracking;
   private ClientTrackingDtoTenant clientTrackingDtoTenant;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        String tenantId = "org_12345";
        clientTracking = testUtilities.createClientTracking(tenantId);
        clientTrackingDtoTenant = testUtilities.createClientTrackingDtoTenant(tenantId);
    }

    @Test
    void clientTrackingDtoTenantToClientTracking_ClientTrackingDtoTenant_returnClientTracking() {
        // Given
        ClientTrackingDtoTenant givenClientTrackingDtoTenant = clientTrackingDtoTenant;

        // When
        ClientTracking actualClientTracking = ClientTrackingMapper.INSTANCE.clientTrackingDtoTenantToClientTracking(givenClientTrackingDtoTenant);

        // Then
        assertEquals(actualClientTracking, clientTracking);
    }

    @Test
    void clientTrackingToClientTrackingDtoTenant_ClientTracking_returnClientTrackingDtoTenant() {
        // Given
        ClientTracking givenClientTracking = clientTracking;

        // When
        ClientTrackingDtoTenant actualClientTrackingDto = ClientTrackingMapper.INSTANCE.clientTrackingToClientTrackingDtoTenant(givenClientTracking);

        // Then
        assertEquals(actualClientTrackingDto, clientTrackingDtoTenant);
    }

    @Test
    void mapping_NullState_ReturnNull() {
        // Given + When
        ClientTrackingDtoTenant givenClientTrackingDtoTenant = ClientTrackingMapper.INSTANCE.clientTrackingToClientTrackingDtoTenant(null);
        ClientTracking givenClientTracking = ClientTrackingMapper.INSTANCE.clientTrackingDtoTenantToClientTracking(null);

        // Then
        assertNull(givenClientTrackingDtoTenant);
        assertNull(givenClientTracking);
    }

    @Test
    void clientTrackingDtoTenantToClientTracking_ClientTrackingDtoTenantIsNull_returnNull() {
        // Given
        ClientTrackingDtoTenant givenClientTrackingDtoTenant = null;

        // When
        ClientTracking actualClientTracking = ClientTrackingMapper.INSTANCE.clientTrackingDtoTenantToClientTracking(givenClientTrackingDtoTenant);

        // Then
        assertNull(actualClientTracking);
    }

    @Test
    void clientTrackingToClientTrackingDtoTenant_ClientTrackingIsNull_returnNull() {
        // Given
        ClientTracking givenClientTracking = null;

        // When
        ClientTrackingDtoTenant actualClientTrackingDtoTenant = ClientTrackingMapper.INSTANCE.clientTrackingToClientTrackingDtoTenant(givenClientTracking);

        // Then
        assertNull(actualClientTrackingDtoTenant);
    }
}