package com.complyt.v1.models.client_tracking;

import com.complyt.security.TenantResolver;
import com.complyt.v1.models.ClientTrackingDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ClientTrackingDtoTest {

    private ClientTrackingDto clientTrackingDto;
    private ClientTrackingDto anotherClientTrackingDto;
    UnitTestUtilities testUtilities;

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
                ", subsidiaries=" + clientTrackingDto.subsidiaries() +
                "]";

        // When
        String actualString = clientTrackingDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
    
}
