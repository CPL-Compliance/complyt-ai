package com.complyt.business.date_injection;

import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.timestamps.Timestamps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExistingTransactionInternalDateInjectorTest {

    ExistingTransactionInternalTimestampsInjector existingTransactionInternalTimestampsInjector;

    Transaction transaction;

    @BeforeEach
    void setup() {
        transaction = createTransaction();
        existingTransactionInternalTimestampsInjector = new ExistingTransactionInternalTimestampsInjector(transaction);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String tenantId = UUID.randomUUID().toString();
        LocalDateTime localDateTime_now = LocalDateTime.now();
        Timestamps internalTimeStamps = new Timestamps(localDateTime_now, localDateTime_now);
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip", false);
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", false);
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, null), false, 0
                        , TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE));
            }
        };

        return Transaction.builder()
                .id(id)
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .tenantId(tenantId)
                .transactionStatus(TransactionStatus.ACTIVE)
                .internalTimestamps(internalTimeStamps)
                .build();
    }

    @Test
    void inject_CurrentDate_ReturnModifiedTransaction() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();

        // When
        Transaction actualTransaction = existingTransactionInternalTimestampsInjector.inject();
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isAfter(beforeActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isBefore(afterActionTime));
    }
}