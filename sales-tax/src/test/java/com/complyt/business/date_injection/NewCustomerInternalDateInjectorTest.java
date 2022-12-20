package com.complyt.business.date_injection;

import com.complyt.business.dates_injection.NewCustomerInternalDateInjector;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class NewCustomerInternalDateInjectorTest {

    NewCustomerInternalDateInjector newCustomerInternalDateInjector;

    Customer customer;

    @BeforeEach
    void setUp() {
        customer = createCustomer();
        newCustomerInternalDateInjector = new NewCustomerInternalDateInjector(customer);
    }

    private Customer createCustomer() {

        return new Customer(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "name",
                null,
                UUID.randomUUID().toString(),
                CustomerType.RETAIL,
                null,
                null
        );
    }

    @Test
    void inject_CurrentDate_ReturnModifiedCustomer() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();

        // When
        Customer actualCustomer = newCustomerInternalDateInjector.inject();
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualCustomer.getInternalTimeStamps().getCreatedDate().compareTo(beforeActionTime) >= 0);
        assertTrue(actualCustomer.getInternalTimeStamps().getUpdatedDate().compareTo(beforeActionTime) >= 0);
        assertTrue(actualCustomer.getInternalTimeStamps().getCreatedDate().compareTo(afterActionTime) <= 0);
        assertTrue(actualCustomer.getInternalTimeStamps().getUpdatedDate().compareTo(afterActionTime) <= 0);
    }
}
