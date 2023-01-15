package com.complyt.business.date_injection;

import com.complyt.business.timestamps_injection.ExistingCustomerInternalTimestampsInjector;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExistingCustomerInternalDateInjectorTest {

    ExistingCustomerInternalTimestampsInjector existingCustomerInternalTimestampsInjector;

    Customer customer;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        customer = domainObjectStub.createCustomer(UUID.randomUUID().toString());
        existingCustomerInternalTimestampsInjector = new ExistingCustomerInternalTimestampsInjector(customer);
    }

    @Test
    void inject_CurrentDate_ReturnModifiedTransaction() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();

        // When
        Customer actualCustomer = existingCustomerInternalTimestampsInjector.inject();
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualCustomer.getInternalTimestamps().getUpdatedDate().getTimestamp().isAfter(beforeActionTime));
        assertTrue(actualCustomer.getInternalTimestamps().getUpdatedDate().getTimestamp().isBefore(afterActionTime));
        assertTrue(actualCustomer.getInternalTimestamps().getCreatedDate().getTimestamp().isBefore(beforeActionTime));
    }
}
