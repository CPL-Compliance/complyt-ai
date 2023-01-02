package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.customer.CustomerTypeDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomerMapperTest {

    String tenantId;
    String externalId;
    private Customer customer;
    private Customer customerNoTenant;
    private CustomerDto customerDto;
    private ObjectId customerId;

    @BeforeEach
    void setup() {
        customerId = new ObjectId();
        tenantId = UUID.randomUUID().toString();
        externalId = UUID.randomUUID().toString();

        customer = createCustomer(tenantId);
        customerNoTenant = createCustomer(null);
        customerDto = createCustomerDto();
    }

    private Customer createCustomer(String tenantId) {
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, tenantId, CustomerType.RETAIL, null, null);
    }

    private CustomerDto createCustomerDto() {
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        return new CustomerDto(customerId.toString(), externalId, name, address, CustomerTypeDto.RETAIL, null, null);
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

}
