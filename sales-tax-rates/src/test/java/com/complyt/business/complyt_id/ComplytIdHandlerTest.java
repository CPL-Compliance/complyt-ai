package com.complyt.business.complyt_id;

import com.complyt.domain.internal_rates.InternalRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ComplytIdHandlerTest {

    ComplytIdHandler<InternalSalesTaxRates> complytIdHandler;

    InternalSalesTaxRates rate;

    @BeforeEach
    void setUp() {
        complytIdHandler = new ComplytIdHandler<>();
        rate = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now());
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasEqualComplytId_ReturnsComplytIdProperty() {
        // Given
        UUID complytId = rate.getComplytId();
        InternalSalesTaxRates newTransaction = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now()).withComplytId(complytId);

        // When
        Mono<InternalSalesTaxRates> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, rate);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasNoComplytId_ReturnsComplytIdProperty() {
        // Given
        InternalSalesTaxRates newTransaction = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now()).withComplytId(null);

        // When
        Mono<InternalSalesTaxRates> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, rate);

        // Then
        StepVerifier.create(transactionMono).expectNext(newTransaction).verifyComplete();
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_HasUnequalComplytId_ReturnsConflictedDataApiException() {
        // Given
        UUID differentComplytId = UUID.randomUUID();
        InternalSalesTaxRates newTransaction = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now()).withComplytId(differentComplytId);

        // When
        Mono<InternalSalesTaxRates> transactionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newTransaction, rate);

        // Then
        StepVerifier.create(transactionMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void checkNewDontHaveComplytId_NullComplytId_ReturnsComplytIdProperty() {
        // Given
        rate = rate.withComplytId(null);

        // When
        Mono<InternalSalesTaxRates> transactionMono = complytIdHandler.checkNewDontHaveComplytId(rate);

        // Then
        StepVerifier.create(transactionMono).expectNext(rate).verifyComplete();
    }

    @Test
    void checkNewDontHaveComplytId_NonNullComplytId_ReturnsConflictedDataApiException() {
        // When
        Mono<InternalSalesTaxRates> transactionMono = complytIdHandler.checkNewDontHaveComplytId(rate);

        // Then
        StepVerifier.create(transactionMono).expectError(ConflictedDataApiException.class).verify();
    }

    @Test
    void insertComplytIdToNew_givenComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        InternalSalesTaxRates givenTransaction = rate.withComplytId(null);

        // When
        InternalSalesTaxRates actualTransaction = complytIdHandler.insertComplytIdToNew(givenTransaction);

        // Then
        assertNotNull(actualTransaction.getComplytId());
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_nullNewComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        InternalSalesTaxRates givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(givenTransaction, rate));

        // Then
        assertEquals("newEntity is marked non-null but is null", exception.getMessage());
    }

    @Test
    void checkComplytIdOfUpdatedEqualsToOld_nullOldComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        InternalSalesTaxRates givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(rate, givenTransaction));

        // Then
        assertEquals("oldEntity is marked non-null but is null", exception.getMessage());
    }

    @Test
    void checkNewDontHaveComplytId_nullComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        InternalSalesTaxRates givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.checkNewDontHaveComplytId(givenTransaction));

        // Then
        assertEquals("newEntity is marked non-null but is null", exception.getMessage());
    }

    @Test
    void insertComplytIdToNew_nullComplytIdProperty_ReturnsComplytIdProperty() {
        // Given
        InternalSalesTaxRates givenTransaction = null;

        // When
        Exception exception = assertThrows(NullPointerException.class, () ->
                complytIdHandler.insertComplytIdToNew(givenTransaction));

        // Then
        assertEquals("newEntity is marked non-null but is null", exception.getMessage());
    }
}