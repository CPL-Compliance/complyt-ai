package com.complyt.v1.models.client_tracking;

import com.complyt.v1.models.ClientTrackingDto;
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
public class ClientTrackingDtoTest {

    private ClientTrackingDto clientTrackingDto;
    private ClientTrackingDto anotherClientTrackingDto;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        clientTrackingDto = testUtilities.createClientTrackingDto();
        anotherClientTrackingDto = clientTrackingDto;
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        assertEquals(clientTrackingDto, anotherClientTrackingDto);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        assertEquals(clientTrackingDto.hashCode(), anotherClientTrackingDto.hashCode());
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "ClientTrackingDto[nexus=" + clientTrackingDto.nexus() +
                ", name=" + clientTrackingDto.name() +
                ", internalTimestamps=" + clientTrackingDto.internalTimestamps() +
                "]";

        // When
        String actualString = clientTrackingDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
    
}
