package com.complyt.business.complyt_id;

import com.complyt.domain.nexus.SalesTaxTracking;
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
class SalesTaxTrackingComplytIdHandlerTest {
    @InjectMocks
    SalesTaxTrackingComplytIdHandler complytIdHandler;
    SalesTaxTracking salesTaxTracking;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxTracking = objectStub.createSalesTaxTracking(new ObjectId().toString());
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_NewDoesntHaveComplytId_ReturnsNewSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null);

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newSalesTaxTracking, salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(newSalesTaxTracking).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreEqual_ReturnsNewSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(salesTaxTracking.getComplytId());

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newSalesTaxTracking, salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(newSalesTaxTracking).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreNotEqual_ReturnsNewSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(UUID.randomUUID());

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newSalesTaxTracking, salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectErrorMessage("400 BAD_REQUEST \"The requested operation failed because there was an unresolvable conflict between two or more inputs.\"").verify();
    }

    @Test
    void isNewDontHaveComplytId_DoesntHaveComplytId_ReturnsNewSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null);

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = complytIdHandler.checkNewDontHaveComplytId(newSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(newSalesTaxTracking).verifyComplete();
    }

    @Test
    void isNewDontHaveComplytId_DoesHaveComplytId_ReturnsEmpty() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(UUID.randomUUID());

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = complytIdHandler.checkNewDontHaveComplytId(newSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectErrorMessage("400 BAD_REQUEST \"The requested operation failed because there was an unresolvable conflict between two or more inputs.\"").verify();
    }

    @Test
    void insertComplytIdToNew_NewSalesTaxTracking_ReturnsWithNewComplytId() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null);

        // When
        SalesTaxTracking actualSalesTaxTracking = complytIdHandler.insertComplytIdToNew(newSalesTaxTracking);

        // Then
        assertNotNull(actualSalesTaxTracking.getComplytId());
    }
}