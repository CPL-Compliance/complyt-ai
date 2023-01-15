package com.complyt.v1.model.customer;

import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.model.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CustomerDtoTest {

    private CustomerDto customerDto;
    private CustomerDto anotherCustomerDto;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        String id = UUID.randomUUID().toString();
        customerDto = domainObjectStub.createCustomerDto(id);
        anotherCustomerDto = customerDto.withId(id);
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
        String expectedString = "CustomerDto(complytId=" + customerDto.getComplytId() +
                ", id=" + customerDto.getId() +
                ", externalId=" + customerDto.getExternalId() +
                ", source=" + customerDto.getSource() +
                ", name=" + customerDto.getName() +
                ", address=" + customerDto.getAddress() +
                ", customerType=" + customerDto.getCustomerType() +
                ", internalTimestamps=" + customerDto.getInternalTimestamps() +
                ", externalTimestamps=" + customerDto.getExternalTimestamps() + ")";

        // When
        String actualString = customerDto.toString();

        // Then
        assertEquals(expectedString, actualString);

    }
}
