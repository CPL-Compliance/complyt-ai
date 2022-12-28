package com.complyt.services;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.timestamps.ComplytTimestamp;
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
public class ExemptionServiceImplTest {

    @InjectMocks
    ExemptionServiceImpl exemptionService;

    @Mock
    ExemptionRepository exemptionRepository;

    Transaction transaction;
    Exemption exemption;
    Customer customer;
    ObjectId customerId = new ObjectId();
    String tenantId = UUID.randomUUID().toString();

    @BeforeEach
    void setUp() {
        customer = createCustomer();
        transaction = createTransaction();
        exemption = createExemption();
    }

    private Customer createCustomer() {
        return new Customer(customerId.toString(), UUID.randomUUID().toString(), "name", null, tenantId, CustomerType.RETAIL, null, null);
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(LocalDateTime.now());
        Timestamps internalTimestamps = new Timestamps(complytTimestamp, complytTimestamp);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), UUID.randomUUID().toString(), new ObjectId(),
                state, classification, validationDates, internalTimestamps, status, certificate, ExemptionType.FULLY);
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();

        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, "CA", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(LocalDateTime.now());
        Timestamps externalTimestamps = new Timestamps(complytTimestamp, complytTimestamp);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, tenantId, null, externalTimestamps, TransactionType.INVOICE, null, null);
    }

    @Test
    void save_SavesExemption_ReturnsExemption() {
        // Given
        Exemption exemptionNoId = exemption.withId(null);

        // When
        when(exemptionRepository.save(exemptionNoId)).thenReturn(Mono.just(exemption));
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
        String id = exemption.getId();

        // When
        when(exemptionRepository.findById(id)).thenReturn(Mono.just(exemption));
        when(exemptionRepository.save(newExemption)).thenReturn(Mono.just(newExemption));
        Mono<Exemption> exemptionMono = exemptionService.update(newExemption, id);

        // Then
        StepVerifier.create(exemptionMono).expectNext(newExemption).verifyComplete();
    }

    @Test
    void update_IdDoesNotExist_ReturnsEmptyMono() {
        // Given
        String id = exemption.getId();

        // When
        when(exemptionRepository.findById(id)).thenReturn(Mono.empty());
        Mono<Exemption> exemptionMono = exemptionService.update(exemption, id);

        // Then
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void isFullyExempted_CustomerHasPartiallyExemptionInState_ReturnsFalse() {
        // Given
        Exemption partialExemption = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When
        when(exemptionRepository.findByClientCustomerAndState(transaction)).thenReturn(Mono.just(partialExemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_CustomerHasFullyExemptionInState_ReturnsTrue() {
        // Given

        // When
        when(exemptionRepository.findByClientCustomerAndState(transaction)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(true).verifyComplete();
    }

    @Test
    void isFullyExempted_NotExemptedBecauseDateExpired_ReturnsFalse() {
        // Given
        ComplytTimestamp createdDate = new ComplytTimestamp(exemption.getValidationDates().getToDate().plusYears(1));
        ComplytTimestamp updatedDate = new ComplytTimestamp(LocalDateTime.now());

        Transaction transactionWithDateLaterThanExemptionDate = transaction
                .withExternalTimestamps(new Timestamps(createdDate, updatedDate));

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithDateLaterThanExemptionDate)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithDateLaterThanExemptionDate);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_NotExemptedBecauseDateIsYetToCome_ReturnsFalse() {
        // Given
        ComplytTimestamp createdDate = new ComplytTimestamp(exemption.getValidationDates().getFromDate().minusYears(1));
        ComplytTimestamp updatedDate = new ComplytTimestamp(LocalDateTime.now());

        Transaction transactionWithDateLaterThanExemptionDate = transaction
                .withExternalTimestamps(new Timestamps(createdDate, updatedDate));

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithDateLaterThanExemptionDate)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithDateLaterThanExemptionDate);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_ExemptionTypeIsPartially_ReturnsFalse() {
        // Given
        Exemption exemptionWithPartiallyType = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When
        when(exemptionRepository.findByClientCustomerAndState(transaction)).thenReturn(Mono.just(exemptionWithPartiallyType));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void delete_DeletesExemption_ReturnsAcknowledgedDeleteResultWithCount1() {
        // Given
        String id = UUID.randomUUID().toString();
        DeleteResult deleteResult = DeleteResult.acknowledged(1);

        // When
        when(exemptionRepository.delete(id)).thenReturn(Mono.just(deleteResult));
        Mono<DeleteResult> deleteResultMono = exemptionService.delete(id);

        // Then
        StepVerifier.create(deleteResultMono).expectNext(deleteResult).verifyComplete();
    }

    @Test
    void delete_NoExemptionFoundToDelete_ReturnsAcknowledgedDeleteResultWithCount0() {
        // Given
        String id = UUID.randomUUID().toString();
        DeleteResult deleteResult = DeleteResult.acknowledged(0);

        // When
        when(exemptionRepository.delete(id)).thenReturn(Mono.just(deleteResult));
        Mono<DeleteResult> deleteResultMono = exemptionService.delete(id);

        // Then
        StepVerifier.create(deleteResultMono).expectNext(deleteResult).verifyComplete();
    }


    @Test
    void delete_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.delete(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void isFullyExemptionActive_ExemptionIsPartially_ThrowsException() {
        // Given
        Exemption partiallyExemption = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When + Then
        when(exemptionRepository.findByClientCustomerAndState(transaction)).thenReturn(Mono.just(partiallyExemption));
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
        String id = UUID.randomUUID().toString();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.update(nullExemption, id));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void update_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.update(exemption, nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
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

}
