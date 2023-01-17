package com.complyt.business.complyt_id;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ExemptionComplytIdHandlerTest {
    @InjectMocks
    ExemptionComplytIdHandler complytIdHandler;
    Exemption exemption;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setup() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        exemption = domainObjectStub.createExemption(new ObjectId().toString());
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_NewDoesntHaveComplytId_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(null);

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.isComplytIdOfUpdatedEqualsToOld(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreEqual_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(exemption.getComplytId());

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.isComplytIdOfUpdatedEqualsToOld(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreNotEqual_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(UUID.randomUUID());

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.isComplytIdOfUpdatedEqualsToOld(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void isNewDontHaveComplytId_DoesntHaveComplytId_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(null);

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.isNewDontHaveComplytId(newExemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void isNewDontHaveComplytId_DoesHaveComplytId_ReturnsEmpty() {
        // Given
        Exemption newExemption = exemption.withComplytId(UUID.randomUUID());

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.isNewDontHaveComplytId(newExemption);

        // Then
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void insertComplytIdToNew_NewExemption_ReturnsWithNewComplytId() {
        // Given
        Exemption newExemption = exemption.withComplytId(null);

        // When
        Exemption actualExemption = complytIdHandler.insertComplytIdToNew(newExemption);

        // Then
        assertNotNull(actualExemption.getComplytId());
    }

}