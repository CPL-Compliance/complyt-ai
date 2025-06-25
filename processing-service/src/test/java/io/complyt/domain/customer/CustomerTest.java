package io.complyt.domain.customer;

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

        anotherCustomer = new Customer(
                customer.complytId(),
                customer.id(),
                customer.externalId(),
                customer.source(),
                customer.name(),
                customer.address(),
                customer.tenantId(),
                customer.email(),
                customer.customerType(),
                customer.internalTimestamps(),
                customer.externalTimestamps(),
                "comment",
                customer.customerStatus()
        );
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
        String expectedString = "Customer[complytId=" + customer.complytId() +
                ", id=" + customer.id() +
                ", externalId=" + customer.externalId() +
                ", source=" + customer.source() +
                ", name=" + customer.name() +
                ", address=" + customer.address() +
                ", tenantId=" + customer.tenantId() +
                ", email=" + customer.email() +
                ", customerType=" + customer.customerType() +
                ", internalTimestamps=" + customer.internalTimestamps() +
                ", externalTimestamps=" + customer.externalTimestamps() +
                ", comment=" + customer.comment() +
                ", customerStatus=" + customer.customerStatus() + "]";

        // When
        String actualString = customer.toString();

        // Then
        assertEquals(expectedString, actualString);
    }
}
