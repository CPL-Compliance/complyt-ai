package com.complyt.v1.mappers;

import com.complyt.domain.customer.Customer;
import com.complyt.security.TenantResolver;
import com.complyt.v1.models.customer.CustomerDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mockStatic;

public class CustomerMapperTest {

    private Customer customer;
    private Customer customerNoTenantNorId;
    private CustomerDto customerDto;
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
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
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
        assertNull(actualCustomer);
    }

    @Test
    void customerToCustomerDto_customerIsNull_returnNull() {
        // Given
        Customer givenCustomer = null;

        // When
        CustomerDto actualCustomerDto = CustomerMapper.INSTANCE.customerToCustomerDto(givenCustomer);

        // Then
        assertNull(actualCustomerDto);
    }
}
