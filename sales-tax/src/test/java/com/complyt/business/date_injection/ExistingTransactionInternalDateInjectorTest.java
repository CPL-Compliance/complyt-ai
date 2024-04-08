package com.complyt.business.date_injection;

import com.complyt.business.timestamps_injection.ExistingTransactionInternalTimestampsInjector;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ExistingTransactionInternalDateInjectorTest {

    private UnitTestUtilities testUtilities;

    private ExistingTransactionInternalTimestampsInjector existingTransactionInternalTimestampsInjector;

    private Transaction transaction;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = createTransaction();
        existingTransactionInternalTimestampsInjector = new ExistingTransactionInternalTimestampsInjector(transaction);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String tenantId = UUID.randomUUID().toString();
        LocalDateTime localDateTime_now = LocalDateTime.now();
        Timestamps internalTimeStamps = new Timestamps(localDateTime_now, localDateTime_now);
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip", "",false);
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", "", false);

        List<Item> items = testUtilities.createItems(true, false, true);

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