package com.complyt.business.date_injection;

import com.complyt.business.dates_injection.ExistingCustomerInternalTimestampsInjector;
import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExistingCustomerInternalDateInjectorTest {

    ExistingCustomerInternalTimestampsInjector existingCustomerInternalTimestampsInjector;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = createCustomer();
        existingCustomerInternalTimestampsInjector = new ExistingCustomerInternalTimestampsInjector(customer);
    }

    private Customer createCustomer() {
        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        TimeStamps externalTimestamps = new TimeStamps(LocalDateTime.now().minusMinutes(1), LocalDateTime.now());
        return new Customer(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "name",
                null,
                UUID.randomUUID().toString(),
                CustomerType.RETAIL,
                internalTimeStamps,
                externalTimestamps
        );
    }

    @Test
    void inject_CurrentDate_ReturnModifiedTransaction() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();

        // When
        Customer actualCustomer = existingCustomerInternalTimestampsInjector.inject();
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualCustomer.getInternalTimeStamps().getUpdatedDate().compareTo(beforeActionTime) >= 0);
        assertTrue(actualCustomer.getInternalTimeStamps().getUpdatedDate().compareTo(afterActionTime) <= 0);
        assertTrue(actualCustomer.getInternalTimeStamps().getCreatedDate().compareTo(beforeActionTime) <= 0);

    }
}
