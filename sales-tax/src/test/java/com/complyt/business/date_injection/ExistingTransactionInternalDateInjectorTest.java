package com.complyt.business.date_injection;

import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import org.bson.types.ObjectId;
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
        ObjectId customerId = new ObjectId();
        String tenantId = UUID.randomUUID().toString();
        Timestamps internalTimeStamps = new Timestamps(LocalDateTime.now(), LocalDateTime.now());
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0
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
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().compareTo(beforeActionTime) >= 0);
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().compareTo(afterActionTime) <= 0);

    }

}