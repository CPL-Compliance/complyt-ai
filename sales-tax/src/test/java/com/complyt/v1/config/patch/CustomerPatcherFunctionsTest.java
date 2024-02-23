package com.complyt.v1.config.patch;

import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.transaction.OptionalAddressDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.UUID;

public class CustomerPatcherFunctionsTest {

    private CustomerDto customer;

    UnitTestUtilities unitTestUtilities;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
        customer = unitTestUtilities.createCustomerDto(UUID.toString());
    }

    @Test
    void patchName_PatchesName_ReturnsModifiedCustomer() {
        // Given
        String nameToPatch = "nameToPatch";
        CustomerDto expectedCustomer = customer.withName(nameToPatch);

        // When
        CustomerDto actualCustomer = CustomerPatcherFunctions.patchName.apply(customer, nameToPatch);

        Assertions.assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void patchAddress_PatchesAddress_ReturnsModifiedCustomer() {
        // Given
        OptionalAddressDto addressToPatch = customer.address().withStreet("10010 Patch Street");
        CustomerDto expectedCustomer = customer.withAddress(addressToPatch);

        // When
        CustomerDto actualCustomer = CustomerPatcherFunctions.patchAddress.apply(customer, addressToPatch);

        // Then
        Assertions.assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void patchCustomerType_PatchesCustomerType_ReturnsModifiedCustomer() {
        // Given
        CustomerTypeDto customerTypeToPatch = CustomerTypeDto.RESELLER;
        CustomerDto expectedCustomer = customer.withCustomerType(customerTypeToPatch);

        // When
        CustomerDto actualCustomer = CustomerPatcherFunctions.patchCustomerType.apply(customer, customerTypeToPatch);

        // Then
        Assertions.assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void patchExternalTimestamps_PatchesExternalTimestamps_ReturnsModifiedCustomer() {
        // Given
        LocalDateTime updatedDate = LocalDateTime.parse(customer.externalTimestamps().updatedDate());
        TimestampsDto externalTimestampsToPatch = customer.externalTimestamps()
                .withUpdatedDate(String.valueOf(updatedDate.plusDays(1)));

        // When
        CustomerDto expectedCustomer = customer.withExternalTimestamps(externalTimestampsToPatch);

        // Then
        CustomerDto actualCustomer = CustomerPatcherFunctions.patchExternalTimestamps.apply(customer, externalTimestampsToPatch);
        Assertions.assertEquals(expectedCustomer, actualCustomer);
    }

    @Test
    void patchEmail_PatchesEmail_ReturnsModifiedCustomer() {
        // Given
        String emailToPatch = "patch@complyt.io";
        CustomerDto expectedCustomer = customer.withEmail(emailToPatch);

        // When
        CustomerDto actualCustomer = CustomerPatcherFunctions.patchEmail.apply(customer, emailToPatch);

        // Then
        Assertions.assertEquals(expectedCustomer, actualCustomer);
    }
}
