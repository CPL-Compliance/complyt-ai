package com.complyt.business.date_injection;

import com.complyt.business.timestamps_injection.NewTransactionInternalTimestampsInjector;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class NewTransactionInternalDateInjectorTest {

    NewTransactionInternalTimestampsInjector newTransactionInternalDateInjector;

    Transaction transaction;

    @BeforeEach
    void setup() {
        transaction = createTransactionWithoutTimeStamps();
        newTransactionInternalDateInjector = new NewTransactionInternalTimestampsInjector(transaction);
    }

    private Transaction createTransactionWithoutTimeStamps() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        String tenantId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRates(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0
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
                .internalTimestamps(null)
                .build();
    }

    @Test
    void inject_CurrentDate_ReturnModifiedTransaction() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();

        // When
        Transaction actualTransaction = newTransactionInternalDateInjector.inject();
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualTransaction.getInternalTimestamps().getCreatedDate().isAfter(beforeActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isAfter(beforeActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getCreatedDate().isBefore(afterActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isBefore(afterActionTime));

    }

}