package com.complyt.v1.model.customer;

import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.timestamps.ComplytTimestampDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CustomerDtoTest {

    private CustomerDto customerDto;
    private CustomerDto anotherCustomerDto;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");

        Timestamps timestamps = createTimestamps();
        TimestampsDto timestampsDto = new TimestampsDto(new ComplytTimestampDto(timestamps.getCreatedDate().getTimestamp().toString()), new ComplytTimestampDto(timestamps.getUpdatedDate().getTimestamp().toString()));
        customerDto = new CustomerDto(id, externalId, name, address, CustomerTypeDto.RETAIL, timestampsDto, timestampsDto);
        anotherCustomerDto = new CustomerDto(customerDto.getId(), customerDto.getExternalId(), customerDto.getName(), customerDto.getAddress(), customerDto.getCustomerType(), timestampsDto, timestampsDto);
    }

    private Timestamps createTimestamps() {
        ComplytTimestamp createdDateTimestamp = new ComplytTimestamp(LocalDateTime.of(2002, 2, 2, 2, 2, 2));
        ComplytTimestamp updatedDateTimestamp = new ComplytTimestamp(LocalDateTime.of(2003, 3, 3, 3, 3, 3));
        return new Timestamps(createdDateTimestamp, updatedDateTimestamp);
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
        String expectedString = "CustomerDto(id=" + customerDto.getId() +
                ", externalId=" + customerDto.getExternalId() +
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
