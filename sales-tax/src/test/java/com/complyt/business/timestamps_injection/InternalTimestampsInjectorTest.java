package com.complyt.business.timestamps_injection;

import com.complyt.domain.timestamps.Timestamps;
import com.complyt.domain.transaction.*;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

public class InternalTimestampsInjectorTest {

    private UnitTestUtilities testUtilities;

    private InternalTimestampsInjector<Transaction> internalTimestampsHandler;

    private Transaction transaction;



    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = createTransaction();
        internalTimestampsHandler = new InternalTimestampsInjector<>();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String tenantId = UUID.randomUUID().toString();
        LocalDateTime localDateTime_now = LocalDateTime.now();
        Timestamps internalTimeStamps = new Timestamps(localDateTime_now, localDateTime_now);
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip", "", false);
        ShippingAddress shippingAddress = new ShippingAddress("City", "Country", "County", "CA", "Street", "Zip", "", false, null);

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
    void insertTimestampsToExisting_CurrentDate_ReturnModifiedTransaction() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();
        Transaction existingTransaction = transaction.setExternalId("existingTransaction");

        // When
        Transaction actualTransaction = internalTimestampsHandler.insertTimestampsToExisting(transaction, existingTransaction);
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isAfter(beforeActionTime) || actualTransaction.getInternalTimestamps().getUpdatedDate().isEqual(beforeActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isBefore(afterActionTime) || actualTransaction.getInternalTimestamps().getUpdatedDate().isEqual(afterActionTime));
    }

    @Test
    void insertTimestampsToExisting_NullNewEntityPassed_ThrowsException() {
        // Given

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            internalTimestampsHandler.insertTimestampsToExisting(null, transaction);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "newEntity is marked non-null but is null");
    }

    @Test
    void insertTimestampsToNew_CurrentDate_ReturnModifiedTransaction() {
        // Given
        LocalDateTime beforeActionTime = LocalDateTime.now();

        // When
        Transaction actualTransaction = internalTimestampsHandler.insertTimestampsToNew(transaction);
        LocalDateTime afterActionTime = LocalDateTime.now();

        // Then
        assertTrue(actualTransaction.getInternalTimestamps().getCreatedDate().isAfter(beforeActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isAfter(beforeActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getCreatedDate().isBefore(afterActionTime));
        assertTrue(actualTransaction.getInternalTimestamps().getUpdatedDate().isBefore(afterActionTime));
    }

    @Test
    void insertTimestampsToExisting_NullExistingEntityPassed_ThrowsException() {
        // Given

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            internalTimestampsHandler.insertTimestampsToExisting(transaction, null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "existingEntity is marked non-null but is null");
    }

    @Test
    void insertTimestampsToNew_NullNewEntityPassed_ThrowsException() {
        // Given

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            internalTimestampsHandler.insertTimestampsToNew(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "newEntity is marked non-null but is null");
    }

}
