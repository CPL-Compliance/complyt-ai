package com.complyt.business.complyt_id;

import com.complyt.domain.Transaction;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class TransactionComplytIdHandlerTest {

    @InjectMocks
    TransactionComplytIdHandler complytIdHandler;
    Transaction transaction;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = objectStub.createTransaction(new ObjectId().toString());
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_NewDoesntHaveComplytId_ReturnsNewTransaction() {
        // Given
        Transaction newTransaction = transaction.withComplytId(null);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreEqual_ReturnsNewTransaction() {
        // Given
        Transaction newTransaction = transaction.withComplytId(transaction.getComplytId());

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreNotEqual_ReturnsNewTransaction() {
        // Given
        Transaction newTransaction = transaction.withComplytId(UUID.randomUUID());

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, transaction);

        // Then
        StepVerifier.create(transactionMono).expectErrorMessage("400 BAD_REQUEST \"The requested operation failed because there was an unresolvable conflict between two or more inputs.\"").verify();
    }

    @Test
    void isNewDontHaveComplytId_DoesntHaveComplytId_ReturnsNewTransaction() {
        // Given
        Transaction newTransaction = transaction.withComplytId(null);

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkNewDontHaveComplytId(newTransaction);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void isNewDontHaveComplytId_DoesHaveComplytId_ReturnsEmpty() {
        // Given
        Transaction newTransaction = transaction.withComplytId(UUID.randomUUID());

        // When
        Mono<Transaction> transactionMono = complytIdHandler.checkNewDontHaveComplytId(newTransaction);

        // Then
        StepVerifier.create(transactionMono).expectErrorMessage("400 BAD_REQUEST \"The requested operation failed because there was an unresolvable conflict between two or more inputs.\"").verify();
    }

    @Test
    void insertComplytIdToNew_NewTransaction_ReturnsWithNewComplytId() {
        // Given
        Transaction newTransaction = transaction.withComplytId(null);

        // When
        Transaction actualTransaction = complytIdHandler.insertComplytIdToNew(newTransaction);

        // Then
        assertNotNull(actualTransaction.getComplytId());
    }

}