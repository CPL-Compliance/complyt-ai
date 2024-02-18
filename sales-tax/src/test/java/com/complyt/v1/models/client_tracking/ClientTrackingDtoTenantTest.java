package com.complyt.v1.models.client_tracking;

import com.complyt.v1.models.ClientTrackingDtoTenant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ClientTrackingDtoTenantTest {

    private ClientTrackingDtoTenant ClientTrackingDtoTenant;
    private ClientTrackingDtoTenant anotherClientTrackingDtoTenant;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        String tenantId = "org_12345";
        ClientTrackingDtoTenant = testUtilities.createClientTrackingDtoTenant(tenantId);
        anotherClientTrackingDtoTenant = ClientTrackingDtoTenant;
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        assertEquals(ClientTrackingDtoTenant, anotherClientTrackingDtoTenant);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        assertEquals(ClientTrackingDtoTenant.hashCode(), anotherClientTrackingDtoTenant.hashCode());
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ClientTrackingDtoTenant[nexus=" + ClientTrackingDtoTenant.nexus() +
                ", name=" + ClientTrackingDtoTenant.name() +
                ", internalTimestamps=" + ClientTrackingDtoTenant.internalTimestamps() +
                ", tenantId=" + ClientTrackingDtoTenant.tenantId() +
                "]";

        // When
        String actualString = ClientTrackingDtoTenant.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}
