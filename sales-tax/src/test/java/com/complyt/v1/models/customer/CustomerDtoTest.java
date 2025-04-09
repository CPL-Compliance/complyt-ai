package com.complyt.v1.models.customer;

import com.complyt.security.TenantResolver;
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
public class CustomerDtoTest {

    private CustomerDto customerDto;
    private CustomerDto anotherCustomerDto;
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
        String id = UUID.randomUUID().toString();
        customerDto = testUtilities.createCustomerDto(id);
        anotherCustomerDto = customerDto.withComplytId(customerDto.complytId());
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        assertEquals(customerDto, anotherCustomerDto);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        assertEquals(customerDto.hashCode(), anotherCustomerDto.hashCode());
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "CustomerDto[complytId=" + customerDto.complytId() +
                ", externalId=" + customerDto.externalId() +
                ", source=" + customerDto.source() +
                ", name=" + customerDto.name() +
                ", address=" + customerDto.address() +
                ", email=" + customerDto.email() +
                ", customerType=" + customerDto.customerType() +
                ", internalTimestamps=" + customerDto.internalTimestamps() +
                ", externalTimestamps=" + customerDto.externalTimestamps() +
                ", comment=" + customerDto.comment() +
                ", customerStatus=" + customerDto.customerStatus() + "]";

        // When
        String actualString = customerDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}