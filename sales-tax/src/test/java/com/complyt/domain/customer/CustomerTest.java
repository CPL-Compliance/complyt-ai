package com.complyt.domain.customer;

import com.complyt.domain.Address;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {

    private Customer customer;
    private Customer anotherCustomer;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String tenantId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        customer = new Customer(id, externalId, name, address, tenantId, CustomerType.RETAIL, null, null);
        anotherCustomer = new Customer(customer.getId(), customer.getExternalId(), customer.getName(), customer.getAddress(), customer.getTenantId(), customer.getCustomerType(), null, null);
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        assertEquals(customer, anotherCustomer);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        assertEquals(customer.hashCode(), anotherCustomer.hashCode());
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "Customer(id=" + customer.getId() +
                ", externalId=" + customer.getExternalId() +
                ", name=" + customer.getName() +
                ", address=" + customer.getAddress() +
                ", tenantId=" + customer.getTenantId() +
                ", customerType=" + customer.getCustomerType() +
                ", internalTimeStamps=" + customer.getInternalTimestamps() +
                ", externalTimeStamps=" + customer.getExternalTimestamps() + ")";

        // When
        String actualString = customer.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}