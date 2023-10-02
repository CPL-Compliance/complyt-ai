package com.complyt.domain.customer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CustomerTest {

    private Customer customer;
    private Customer anotherCustomer;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
        anotherCustomer = new Customer(customer.getComplytId(), customer.getId(), customer.getExternalId(), customer.getSource(), customer.getName(), customer.getAddress(), customer.getTenantId(), customer.getEmail(), customer.getCustomerType(), customer.getInternalTimestamps(), customer.getExternalTimestamps(), "comment");
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
                ", email=" + customer.getEmail() +
                ", customerType=" + customer.getCustomerType() +
                ", internalTimestamps=" + customer.getInternalTimestamps() +
                ", externalTimestamps=" + customer.getExternalTimestamps() +
                ", comment=" + customer.getComment() + ")";

        // When
        String actualString = customer.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

}