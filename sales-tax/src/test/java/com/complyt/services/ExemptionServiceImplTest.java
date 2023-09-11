package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.exemption.ExemptionListGenerator;
import com.complyt.domain.State;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.repositories.ExemptionRepository;
import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class ExemptionServiceImplTest {

    @InjectMocks
    ExemptionServiceImpl exemptionService;

    @Mock
    ExemptionRepository exemptionRepository;

    @Mock
    ComplytIdHandler<Exemption> exemptionComplytIdHandler;

    @Mock
    ExemptionListGenerator exemptionListGenerator;

    Transaction transaction;
    Exemption exemption;
    Customer customer;
    ObjectId customerId = new ObjectId();
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(customerId.toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        exemption = testUtilities.createExemption(UUID.randomUUID().toString());
    }

    @Test
    void save_SavesExemption_ReturnsExemption() {
        // Given
        Exemption exemptionNoId = exemption.withId(null);
        Exemption exemptionWithNewComplytId = exemptionNoId.withComplytId(exemption.getComplytId());
        when(exemptionComplytIdHandler.checkNewDontHaveComplytId(exemptionNoId)).thenReturn(Mono.just(exemption));
        when(exemptionComplytIdHandler.insertComplytIdToNew(exemption)).thenReturn(exemptionWithNewComplytId);
        Exemption exemptionWithIdAndComplytId = exemptionWithNewComplytId.withId(exemption.getId());

        // When
        when(exemptionRepository.save(exemptionWithNewComplytId)).thenReturn(Mono.just(exemptionWithIdAndComplytId));
        Mono<Exemption> exemptionMono = exemptionService.save(exemptionNoId);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @Test
    void findById_FindsExemption_ReturnsExemption() {
        // Given
        String id = exemption.getId();

        // When
        when(exemptionRepository.findById(id)).thenReturn(Mono.just(exemption));
        Mono<Exemption> exemptionMono = exemptionService.findById(id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @Test
    void findById_IdDoesNotExist_ReturnsEmptyMono() {
        // Given
        String id = exemption.getId();

        // When
        when(exemptionRepository.findById(id)).thenReturn(Mono.empty());
        Mono<Exemption> exemptionMono = exemptionService.findById(id);

        // Then
        StepVerifier.create(exemptionMono).verifyComplete();
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
        when(exemptionRepository.findAll()).thenReturn(Flux.fromIterable(exemptions));
        Flux<Exemption> exemptionFlux = exemptionService.findAll();

        // Then
        StepVerifier.create(exemptionFlux).expectNext(exemption, secondExemption).verifyComplete();
    }

    @Test
    void update_UpdatesExemption_ReturnsUpdatedExemption() {
        // Given
        Exemption newExemption = exemption.withStatus(new Status("new code", "new name"));
        UUID id = exemption.getComplytId();

        // When
        when(exemptionRepository.save(newExemption)).thenReturn(Mono.just(newExemption));
        Mono<Exemption> exemptionMono = exemptionService.update(newExemption, exemption, id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void isFullyExempted_CustomerHasPartiallyExemptionInState_ReturnsFalse() {
        // Given
        Exemption partialExemption = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When
        when(exemptionRepository.findByCustomerAndState(transaction.getCustomerId(), transaction.getShippingAddress().state())).thenReturn(Mono.just(partialExemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_CustomerHasFullyExemptionInState_ReturnsTrue() {
        // Given

        // When
        when(exemptionRepository.findByCustomerAndState(transaction.getCustomerId(), transaction.getShippingAddress().state())).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(true).verifyComplete();
    }

    @Test
    void isFullyExempted_NotExemptedBecauseDateExpired_ReturnsFalse() {
        // Given
        LocalDateTime createdDate = exemption.getValidationDates().getToDate().plusYears(1);
        LocalDateTime updatedDate = LocalDateTime.now();

        Transaction transactionWithDateLaterThanExemptionDate = transaction
                .withExternalTimestamps(new Timestamps(createdDate, updatedDate));

        // When
        when(exemptionRepository.findByCustomerAndState(transactionWithDateLaterThanExemptionDate.getCustomerId(), transactionWithDateLaterThanExemptionDate.getShippingAddress().state())).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithDateLaterThanExemptionDate);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_NotExemptedBecauseDateIsYetToCome_ReturnsFalse() {
        // Given
        LocalDateTime createdDate = exemption.getValidationDates().getFromDate().minusYears(1);
        LocalDateTime updatedDate = LocalDateTime.now();

        Transaction transactionWithDateLaterThanExemptionDate = transaction
                .withExternalTimestamps(new Timestamps(createdDate, updatedDate));

        // When
        when(exemptionRepository.findByCustomerAndState(transactionWithDateLaterThanExemptionDate.getCustomerId(), transactionWithDateLaterThanExemptionDate.getShippingAddress().state())).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithDateLaterThanExemptionDate);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_ExemptionTypeIsPartially_ReturnsFalse() {
        // Given
        Exemption exemptionWithPartiallyType = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When
        when(exemptionRepository.findByCustomerAndState(transaction.getCustomerId(), transaction.getShippingAddress().state())).thenReturn(Mono.just(exemptionWithPartiallyType));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void delete_DeletesExemption_ReturnsCancelledExemption() {
        // Given
        UUID id = UUID.randomUUID();
        Exemption cancelledExemption = exemption.withExemptionStatus(ExemptionStatus.CANCELLED);

        // When
        when(exemptionRepository.findByComplytId(id)).thenReturn(Mono.just(exemption));
        when(exemptionRepository.save(cancelledExemption)).thenReturn(Mono.just(cancelledExemption));
        Mono<Exemption> deleteResultMono = exemptionService.markAsCancelled(id);

        // Then
        StepVerifier.create(deleteResultMono).expectNext(cancelledExemption).verifyComplete();
    }

    @Test
    void delete_NoExemptionFoundToDelete_ReturnsMonoEmpty() {
        // Given
        UUID id = UUID.randomUUID();
        DeleteResult deleteResult = DeleteResult.acknowledged(0);

        // When
        when(exemptionRepository.findByComplytId(id)).thenReturn(Mono.empty());
        Mono<Exemption> exemptionMono = exemptionService.markAsCancelled(id);

        // Then
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void checkExemptionNotHavingComplytId_DoesntHaveComplytId_ReturnsExemption() {
        // Given When
        when(exemptionComplytIdHandler.checkNewDontHaveComplytId(exemption)).thenReturn(Mono.just(exemption));
        Mono<Exemption> exemptionMono = exemptionService.checkExemptionNotHavingComplytId(exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @Test
    void checkExemptionNotHavingComplytId_DoesHaveComplytId_ThrowsException() {
        // Given When
        when(exemptionComplytIdHandler.checkNewDontHaveComplytId(exemption)).thenReturn(Mono.error(new NotFoundException("cannot insert new exemption with complyt id")));
        Mono<Exemption> exemptionMono = exemptionService.checkExemptionNotHavingComplytId(exemption);

        // Then
        StepVerifier.create(exemptionMono).expectErrorMessage("cannot insert new exemption with complyt id").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdMotEquals_ThrowsExceptions() {
        // Given
        Exemption newExemption = exemption.withComplytId(UUID.randomUUID());

        // When
        when(exemptionComplytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newExemption, exemption)).thenReturn(Mono.error(new NotFoundException("complyt ids of modified and original exemptions are not equal")));
        Mono<Exemption> exemptionMono = exemptionService.checkComplytIdOfModifiedEqualsToOriginal(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectErrorMessage("complyt ids of modified and original exemptions are not equal").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_DoesNotHaveComplytId_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(null);

        // When
        when(exemptionComplytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newExemption, exemption)).thenReturn(Mono.just(newExemption));
        Mono<Exemption> exemptionMono = exemptionService.checkComplytIdOfModifiedEqualsToOriginal(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdAreEquals_ReturnsNewExemption() {
        // Given
        Exemption newExemption = exemption.withComplytId(exemption.getComplytId());

        // When
        when(exemptionComplytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newExemption, exemption)).thenReturn(Mono.just(newExemption));
        Mono<Exemption> exemptionMono = exemptionService.checkComplytIdOfModifiedEqualsToOriginal(newExemption, exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void findByComplytId_complytIdExists_ReturnsExemption() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        when(exemptionRepository.findByComplytId(complytId)).thenReturn(Mono.just(exemption.withComplytId(complytId)));
        Mono<Exemption> exemptionMono = exemptionService.findByComplytId(complytId);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void injectDataToNewExemption_notNullExemption_ReturnsExemptionWithData() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        when(exemptionComplytIdHandler.insertComplytIdToNew(exemption)).thenReturn(exemption.withComplytId(complytId));
        Mono<Exemption> exemptionMono = exemptionService.injectDataToNewExemption(exemption);

        // Then
        StepVerifier.create(exemptionMono).expectNext(exemption.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void saveMany_Saves3Exemptions_ReturnsExemptions() {
        // Given
        List<State> states = UnitTestUtilities.createStateList();
        Exemption exemptionNoIds = exemption.withComplytId(null).withId(null);
        ExemptionWrapper exemptionWrapper = new ExemptionWrapper(exemptionNoIds, states);
        List<Exemption> exemptions = UnitTestUtilities.createExemptionsListFromWrapper(exemptionWrapper);

        Exemption firstExemptionWithComplytId = exemptions.get(0).withComplytId(UUID.randomUUID());
        Exemption secondExemptionWithComplytId = exemptions.get(1).withComplytId(UUID.randomUUID());
        Exemption thirdExemptionWithComplytId = exemptions.get(2).withComplytId(UUID.randomUUID());

        Exemption firstExemptionWithIds = firstExemptionWithComplytId.withId(UUID.randomUUID().toString());
        Exemption secondExemptionWithIds = secondExemptionWithComplytId.withId(UUID.randomUUID().toString());
        Exemption thirdExemptionWithIds = thirdExemptionWithComplytId.withId(UUID.randomUUID().toString());

        // When
        when(exemptionListGenerator.generate(exemptionWrapper)).thenReturn(Flux.fromIterable(exemptions));

        when(exemptionComplytIdHandler.checkNewDontHaveComplytId(exemptions.get(0))).thenReturn(Mono.just(exemptions.get(0)));
        when(exemptionComplytIdHandler.checkNewDontHaveComplytId(exemptions.get(1))).thenReturn(Mono.just(exemptions.get(1)));
        when(exemptionComplytIdHandler.checkNewDontHaveComplytId(exemptions.get(2))).thenReturn(Mono.just(exemptions.get(2)));

        when(exemptionComplytIdHandler.insertComplytIdToNew(exemptions.get(0))).thenReturn(firstExemptionWithComplytId);
        when(exemptionComplytIdHandler.insertComplytIdToNew(exemptions.get(1))).thenReturn(secondExemptionWithComplytId);
        when(exemptionComplytIdHandler.insertComplytIdToNew(exemptions.get(2))).thenReturn(thirdExemptionWithComplytId);

        when(exemptionRepository.save(firstExemptionWithComplytId)).thenReturn(Mono.just(firstExemptionWithIds));
        when(exemptionRepository.save(secondExemptionWithComplytId)).thenReturn(Mono.just(secondExemptionWithIds));
        when(exemptionRepository.save(thirdExemptionWithComplytId)).thenReturn(Mono.just(thirdExemptionWithIds));

        Flux<Exemption> exemptionFlux = exemptionService.saveMany(exemptionWrapper);

        // Then
        StepVerifier.create(exemptionFlux)
                .expectNext(firstExemptionWithIds)
                .expectNext(secondExemptionWithIds)
                .expectNext(thirdExemptionWithIds)
                .verifyComplete();

    }

    @Test
    void delete_NullIdPassed_ThrowsException() {
        // Given
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.markAsCancelled(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void isFullyExemptionActive_ExemptionIsPartially_ThrowsException() {
        // Given
        Exemption partiallyExemption = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When + Then
        when(exemptionRepository.findByCustomerAndState(transaction.getCustomerId(), transaction.getShippingAddress().state())).thenReturn(Mono.just(partiallyExemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void findByClientCustomerAndState_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.findByClientCustomerAndState(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void IsFullyExempted_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.isFullyExempted(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.findById(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void update_NullExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;
        UUID id = UUID.randomUUID();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.update(nullExemption, exemption, id));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void update_NullOriginalExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;
        UUID id = UUID.randomUUID();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.update(exemption, nullExemption, id));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalExemption is marked non-null but is null");
    }

    @Test
    void update_NullIdPassed_ThrowsException() {
        // Given
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.update(exemption, exemption, nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void save_NullExemption_ThrowsException() {
        // Given
        Exemption nullExemption = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.save(nullExemption));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void checkCustomerNotHavingComplytId_NullGiven_ThrowsNullPointerException() {
        // Given
        Exemption nullExemption = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionService.checkExemptionNotHavingComplytId(nullExemption);
        });

        assertEquals(nullPointerException.getMessage(), "newExemption is marked non-null but is null");
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_NullModifiedExemption_ThrowsNullPointerException() {
        // Given
        Exemption nullExemption = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionService.checkComplytIdOfModifiedEqualsToOriginal(nullExemption, exemption);
        });

        assertEquals(nullPointerException.getMessage(), "modifiedExemption is marked non-null but is null");
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_NullOriginalExemption_ThrowsNullPointerException() {
        // Given
        Exemption nullExemption = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionService.checkComplytIdOfModifiedEqualsToOriginal(exemption, nullExemption);
        });

        assertEquals(nullPointerException.getMessage(), "originalExemption is marked non-null but is null");
    }

    @Test
    void injectDataToNewExemption_NullExemption_ThrowsNullPointerException() {
        // Given
        Exemption nullExemption = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionService.injectDataToNewExemption(nullExemption);
        });

        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void findByComplytId_NullExemption_ThrowsNullPointerException() {
        // Given
        UUID nullComplytId = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            exemptionService.findByComplytId(nullComplytId);
        });

        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void saveMany_NullExemptionWrapperPassed_ThrowsException() {
        // Given
        ExemptionWrapper nullExemptionWrapper = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.saveMany(nullExemptionWrapper));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemptionWrapper is marked non-null but is null");
    }
}
