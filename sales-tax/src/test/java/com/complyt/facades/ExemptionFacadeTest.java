package com.complyt.facades;

import com.complyt.domain.State;
import com.complyt.domain.TimeStamps;
import com.complyt.domain.customer.exemption.*;
import com.complyt.services.ExemptionServiceImpl;
import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExemptionFacadeTest {

    @InjectMocks
    ExemptionFacade exemptionFacade;

    @Mock
    ExemptionServiceImpl exemptionService;

    Exemption exemption;

    @BeforeEach
    void setUp() {
        exemption = createExemption();
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), new ObjectId(), new ObjectId(),
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
    }

    @Test
    void save_ExemptionSaved_ExemptionReturned() {
        // Given
        Exemption exemptionNoId = exemption.withId(null);

        // When
        when(exemptionService.save(exemptionNoId)).thenReturn(Mono.just(exemption));
        Mono<Exemption> exemptionMono = exemptionFacade.save(exemptionNoId);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @Test
    void save_NullExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.save(nullExemption));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void findById_FindsExemption_ReturnsExemption() {
        // Given
        String id = exemption.getId();

        // When
        when(exemptionService.findById(id)).thenReturn(Mono.just(exemption));
        Mono<Exemption> exemptionMono = exemptionFacade.findById(id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.findById(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void findAll_FindsTwoExemptions_ReturnsTwoExemptions() {
        // Given
        Exemption secondExemption = exemption.withId(UUID.randomUUID().toString())
                .withState(new State("NY", "05", "New York"));
        List<Exemption> exemptions = new ArrayList<Exemption>() {{
            add(exemption);
            add(secondExemption);
        }};

        // When
        when(exemptionService.findAll()).thenReturn(Flux.fromIterable(exemptions));
        Flux<Exemption> exemptionFlux = exemptionFacade.findAll();

        // Then
        StepVerifier.create(exemptionFlux).expectNext(exemption, secondExemption);
    }

    @Test
    void update_UpdatesExemption_ReturnsUpdatedExemption() {
        // Given
        Exemption newExemption = exemption.withStatus(new Status("new code", "new name"));
        String id = exemption.getId();

        // When
        when(exemptionService.update(newExemption, id)).thenReturn(Mono.just(newExemption));
        Mono<Exemption> exemptionMono = exemptionFacade.update(newExemption, id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void delete_DeletesExemption_ReturnsAcknowledgedDeleteResultWithCount1() {
        // Given
        String id = UUID.randomUUID().toString();
        DeleteResult deleteResult = DeleteResult.acknowledged(1);

        // When
        when(exemptionService.delete(id)).thenReturn(Mono.just(deleteResult));
        Mono<DeleteResult> deleteResultMono = exemptionFacade.delete(id);

        // Then
        StepVerifier.create(deleteResultMono).expectNext(deleteResult).verifyComplete();
    }

    @Test
    void delete_NoExemptionFoundToDelete_ReturnsAcknowledgedDeleteResultWithCount0() {
        // Given
        String id = UUID.randomUUID().toString();
        DeleteResult deleteResult = DeleteResult.acknowledged(0);

        // When
        when(exemptionService.delete(id)).thenReturn(Mono.just(deleteResult));
        Mono<DeleteResult> deleteResultMono = exemptionFacade.delete(id);

        // Then
        StepVerifier.create(deleteResultMono).expectNext(deleteResult).verifyComplete();
    }


    @Test
    void delete_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.delete(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void update_NullExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;
        String id = UUID.randomUUID().toString();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.update(nullExemption,id));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void update_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionFacade.update(exemption,nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

}
