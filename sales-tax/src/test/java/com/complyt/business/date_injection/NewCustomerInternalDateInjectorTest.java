package com.complyt.business.date_injection;

import com.complyt.business.timestamps_injection.NewCustomerInternalTimestampsInjector;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewCustomerInternalDateInjectorTest {

    NewCustomerInternalTimestampsInjector newCustomerInternalDateInjector;

    Customer customer;

    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customer = objectStub.createCustomer(UUID.randomUUID().toString());
        newCustomerInternalDateInjector = new NewCustomerInternalTimestampsInjector(customer);
    }

    @Test
    void inject_CurrentDate_ReturnModifiedCustomer() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();

        // When
        Customer actualCustomer = newCustomerInternalDateInjector.inject();
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualCustomer.getInternalTimestamps().getCreatedDate().isAfter(beforeActionTime));
        assertTrue(actualCustomer.getInternalTimestamps().getUpdatedDate().isAfter(beforeActionTime));
        assertTrue(actualCustomer.getInternalTimestamps().getCreatedDate().isBefore(afterActionTime));
        assertTrue(actualCustomer.getInternalTimestamps().getUpdatedDate().isBefore(afterActionTime));
    }
}
