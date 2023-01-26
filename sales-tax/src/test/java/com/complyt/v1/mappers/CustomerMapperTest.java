package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.AddressDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.timestamps.ComplytTimestampDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerMapperTest {

    String tenantId;
    String externalId;
    private Customer customer;
    private Customer customerNoTenant;
    private CustomerDto customerDto;
    private ObjectId customerId;

    private Timestamps timestamps;
    @BeforeEach
    void setup() {
        customerId = new ObjectId();
        tenantId = UUID.randomUUID().toString();
        externalId = UUID.randomUUID().toString();
        timestamps = createTimestamps();
        customer = createCustomer(tenantId);
        customerNoTenant = createCustomer(null);
        customerDto = createCustomerDto();
    }

    private Timestamps createTimestamps() {
        ComplytTimestamp createdDateTimestamp = new ComplytTimestamp(LocalDateTime.of(2002, 2, 2, 2, 2, 2));
        ComplytTimestamp updatedDateTimestamp = new ComplytTimestamp(LocalDateTime.of(2003, 3, 3, 3, 3, 3));
        return new Timestamps(createdDateTimestamp, updatedDateTimestamp);
    }

    private Customer createCustomer(String tenantId) {
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");

        return new Customer(customerId.toString(), externalId, name, address, tenantId, CustomerType.RETAIL, timestamps, timestamps);
    }

    private CustomerDto createCustomerDto() {
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        TimestampsDto timestampsDto = new TimestampsDto(new ComplytTimestampDto(timestamps.getCreatedDate().getTimestamp().toString()), new ComplytTimestampDto(timestamps.getUpdatedDate().getTimestamp().toString()));

        return new CustomerDto(customerId.toString(), externalId, name, address, CustomerTypeDto.RETAIL, timestampsDto, timestampsDto);
    }

    @Test
    void customerToCustomerDto_Customer_returnCustomerDto() {
        // Given
        Customer givenCustomer = customer;

        // When
        CustomerDto actualCustomerDto = CustomerMapper.INSTANCE.customerToCustomerDto(givenCustomer);

        // Then
        assertEquals(customerDto, actualCustomerDto);
    }

    @Test
    void customerDtoToCustomer_CustomerDto_returnCustomer() {

        // Given
        CustomerDto givenCustomerDto = customerDto;

        // When
        Customer actualCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);

        // Then
        assertEquals(customerNoTenant, actualCustomer);
    }

    @Test
    void customerDtoToCustomer_CustomerDtoIsNull_returnNull() {

        // Given
        CustomerDto givenCustomerDto = null;

        // When
        Customer actualCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(givenCustomerDto);

        // Then
        assertEquals(null, actualCustomer);
    }

    @Test
    void customerToCustomerDto_customerIsNull_returnNull() {
        // Given
        Customer givenCustomer = null;

        // When
        CustomerDto actualCustomerDto = CustomerMapper.INSTANCE.customerToCustomerDto(givenCustomer);

        // Then
        assertEquals(givenCustomer, actualCustomerDto);
    }
}
