package com.complyt.v1.mappers;

import com.complyt.domain.customer.Customer;
import com.complyt.v1.models.customer.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class CustomerMapperTest {

    private Customer customer;
    private Customer customerNoTenantNorId;
    private CustomerDto customerDto;
    TestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
        customerNoTenantNorId = testUtilities.createCustomer(customer.getId()).withTenantId(null).withComplytId(customer.getComplytId()).withId(null);
        customerDto = testUtilities.createCustomerDto(customer.getId()).withComplytId(customer.getComplytId());
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
