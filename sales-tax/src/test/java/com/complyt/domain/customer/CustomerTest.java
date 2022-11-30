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
        customer = new Customer(id, externalId, name, address, tenantId, CustomerType.RETAIL);
        anotherCustomer = new Customer(customer.getId(), customer.getExternalId(), customer.getName(), customer.getAddress(), customer.getTenantId(), customer.getCustomerType());
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
        String expectedString = "Customer(id=" + customer.getId() + ", externalId=" + customer.getExternalId() + ", name=Existing Customer, address=Address(city=City, country=Country, county=County, state=State, street=Street, zip=Zip), tenantId=" + customer.getTenantId() + ", customerType=RETAIL)";

        // When
        String actualString = customer.toString();

        // Then
        assertEquals(expectedString,actualString);

    }

}