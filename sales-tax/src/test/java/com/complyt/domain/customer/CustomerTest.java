package com.complyt.domain.customer;

import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {

    private Customer customer;
    private Customer anotherCustomer;

    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customer = objectStub.createCustomer(UUID.randomUUID().toString());
        anotherCustomer = new Customer(customer.getComplytId(), customer.getId(), customer.getExternalId(), customer.getSource(), customer.getName(), customer.getAddress(), customer.getTenantId(), customer.getCustomerType(), customer.getInternalTimestamps(), customer.getExternalTimestamps());
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
        String expectedString = "Customer(complytId=" + customer.getComplytId() +
                ", id=" + customer.getId() +
                ", externalId=" + customer.getExternalId() +
                ", source=" + customer.getSource() +
                ", name=" + customer.getName() +
                ", address=" + customer.getAddress() +
                ", tenantId=" + customer.getTenantId() +
                ", customerType=" + customer.getCustomerType() +
                ", internalTimestamps=" + customer.getInternalTimestamps() +
                ", externalTimestamps=" + customer.getExternalTimestamps() + ")";

        // When
        String actualString = customer.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}