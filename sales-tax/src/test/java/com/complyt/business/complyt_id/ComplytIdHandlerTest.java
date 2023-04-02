package com.complyt.business.complyt_id;

import com.complyt.domain.Transaction;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ut.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ComplytIdHandlerTest {

    ComplytIdHandler<Transaction> complytIdHandler;

    Transaction transaction;
    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        complytIdHandler = new ComplytIdHandler<>();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasEqualComplytId_ReturnsComplytIdProperty() {
        // Given
        UUID complytId = transaction.getComplytId();
        Transaction newTransaction = testUtilities.createTransaction(UUID.randomUUID().toString()).withComplytId(complytId);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasNoComplytId_ReturnsComplytIdProperty() {
        // Given
        Transaction newTransaction = testUtilities.createTransaction(UUID.randomUUID().toString()).withComplytId(null);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasUnequalComplytId_ReturnsConflictedDataApiException() {
        // Given
        UUID differentComplytId = UUID.randomUUID();
        Transaction newTransaction = testUtilities.createTransaction(UUID.randomUUID().toString()).withComplytId(differentComplytId);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void checkNewDontHaveComplytId_NullComplytId_ReturnsComplytIdProperty() {
        // Given
        transaction = transaction.withComplytId(null);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkNewDontHaveComplytId(transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(transaction).verifyComplete();
    }

    @Test
    void checkNewDontHaveComplytId_NonNullComplytId_ReturnsConflictedDataApiException() {
        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkNewDontHaveComplytId(transaction);

        // Then
        StepVerifier.create(transactionMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void insertComplytIdToNew_givenComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        Transaction givenTransaction = transaction.withComplytId(null);

        // When
        Transaction actualTransaction = complytIdHandler.insertComplytIdToNew(givenTransaction);

        // Then
        assertNotNull(actualTransaction.getComplytId());
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_nullNewComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        Transaction givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(givenTransaction, transaction));

        // Then
        assertEquals("newEntity is marked non-null but is null", exception.getMessage());
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_nullOldComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        Transaction givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(transaction, givenTransaction));

        // Then
        assertEquals("oldEntity is marked non-null but is null", exception.getMessage());
    }

    @Test
    void checkNewDontHaveComplytId_nullComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        Transaction givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.checkNewDontHaveComplytId(givenTransaction));

        // Then
        assertEquals("newEntity is marked non-null but is null", exception.getMessage());
    }

    @Test
    void insertComplytIdToNew_nullComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        Transaction givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.insertComplytIdToNew(givenTransaction));

        // Then
        assertEquals("newEntity is marked non-null but is null", exception.getMessage());
    }

}