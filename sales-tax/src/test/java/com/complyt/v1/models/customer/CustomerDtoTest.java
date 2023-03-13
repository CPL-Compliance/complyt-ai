package com.complyt.v1.models.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CustomerDtoTest {

    private CustomerDto customerDto;
    private CustomerDto anotherCustomerDto;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
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
                ", customerType=" + customerDto.customerType() +
                ", internalTimestamps=" + customerDto.internalTimestamps() +
                ", externalTimestamps=" + customerDto.externalTimestamps() + "]";

        // When
        String actualString = customerDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
