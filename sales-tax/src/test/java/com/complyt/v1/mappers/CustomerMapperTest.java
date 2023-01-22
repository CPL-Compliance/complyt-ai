package com.complyt.v1.mappers;

import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.model.customer.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomerMapperTest {

    private Customer customer;
    private Customer customerNoTenantNorId;
    private CustomerDto customerDto;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customer = domainObjectStub.createCustomer(UUID.randomUUID().toString());
        customerNoTenantNorId = domainObjectStub.createCustomer(customer.getId()).withTenantId(null).withComplytId(customer.getComplytId()).withId(null);
        customerDto = domainObjectStub.createCustomerDto(customer.getId()).withComplytId(customer.getComplytId());
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

}
