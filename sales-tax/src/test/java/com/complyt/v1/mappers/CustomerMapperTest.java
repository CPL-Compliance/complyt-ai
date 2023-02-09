package com.complyt.v1.mappers;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.models.customer.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomerMapperTest {

    private Customer customer;
    private Customer customerNoTenantNorId;
    private CustomerDto customerDto;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customer = objectStub.createCustomer(UUID.randomUUID().toString());
        customerNoTenantNorId = objectStub.createCustomer(customer.getId()).withTenantId(null).withComplytId(customer.getComplytId()).withId(null);
        customerDto = objectStub.createCustomerDto(customer.getId()).withComplytId(customer.getComplytId());
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
        assertEquals(customerNoTenantNorId, actualCustomer);
    }

    @Test
    void mapping_NullState_ReturnNull() {
        // Given + When
        Customer givenCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(null);
        CustomerDto givenCustomerDto = CustomerMapper.INSTANCE.customerToCustomerDto(null);

        // Then
        assertNull(givenCustomer);
        assertNull(givenCustomerDto);
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
