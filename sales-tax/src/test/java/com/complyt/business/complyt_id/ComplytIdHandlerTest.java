package com.complyt.business.complyt_id;

import com.complyt.domain.Transaction;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

class ComplytIdHandlerTest {

    ComplytIdHandler<Transaction> complytIdHandler;

    Transaction transaction;
    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        complytIdHandler = new ComplytIdHandler<>();
        transaction = objectStub.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasEqualComplytId_ReturnsComplytIdProperty() {
        // Given
        UUID complytId = transaction.getComplytId();
        Transaction newTransaction = objectStub.createTransaction(UUID.randomUUID().toString()).withComplytId(complytId);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction,transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasNoComplytId_ReturnsComplytIdProperty() {
        // Given
        Transaction newTransaction = objectStub.createTransaction(UUID.randomUUID().toString()).withComplytId(null);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction,transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasUnequalComplytId_ReturnsConflictedDataApiException() {
        // Given
        UUID differentComplytId = UUID.randomUUID();
        Transaction newTransaction = objectStub.createTransaction(UUID.randomUUID().toString()).withComplytId(differentComplytId);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction,transaction);

        // Then
        StepVerifier.create(transactionMono).expectError(ConflictedDataApiException.class).verify();
    }
}