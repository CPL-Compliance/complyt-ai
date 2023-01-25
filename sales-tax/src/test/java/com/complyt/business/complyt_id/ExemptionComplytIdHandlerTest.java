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
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ExemptionComplytIdHandlerTest {
    @InjectMocks
    ExemptionComplytIdHandler complytIdHandler;
    Exemption exemption;
    ObjectStub objectStub;

    @BeforeEach
    void setup() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        exemption = objectStub.createExemption(new ObjectId().toString());
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_NewDoesntHaveComplytId_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(null);

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreEqual_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(exemption.getComplytId());

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void isComplytIdOfUpdatedEqualsToOld_ComplytIdsAreNotEqual_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(UUID.randomUUID());

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectErrorMessage("complyt ids of modified and original exemptions are not equal").verify();
    }

    @Test
    void isNewDontHaveComplytId_DoesntHaveComplytId_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(null);

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.checkNewDontHaveComplytId(newExemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void isNewDontHaveComplytId_DoesHaveComplytId_ReturnsEmpty() {
        // Given
        Exemption newExemption = exemption.withComplytId(UUID.randomUUID());

        // When
        Mono<Exemption> exemptionMono = complytIdHandler.checkNewDontHaveComplytId(newExemption);

        // Then
        StepVerifier.create(exemptionMono).expectErrorMessage("cannot insert new exemption with complyt id").verify();
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