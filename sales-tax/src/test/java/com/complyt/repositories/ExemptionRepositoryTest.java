package com.complyt.repositories;

import com.complyt.domain.*;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.security.TenantResolver;
import com.mongodb.client.result.DeleteResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
public class ExemptionRepositoryTest {

    @InjectMocks
    ExemptionRepository exemptionRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    TenantResolver tenantResolver;

    private String tenantId;

    Exemption exemption;

    Transaction transaction;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tenantId = UUID.randomUUID().toString();
        exemption = createExemption();
        transaction = createTransaction();
    }

    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();

        Address billingAddress = new Address("City", "Country", null, "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", null, exemption.getState().getAbbreviation(), "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, exemption.getCustomerId(), null, null, TransactionStatus.ACTIVE, exemption.getTenantId(), null, externalTimeStamps, TransactionType.INVOICE, null);
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), tenantId, new ObjectId(),
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
    }

    @Test
    void findByClientCustomerAndState_FindsExemption_ReturnsExemption() {
        // Given
        Query query = Query.query(Criteria
                .where("tenantId").is(tenantId)
                .and("customerId").is(transaction.getCustomerId())
                .and("state.abbreviation").is(transaction.getShippingAddress().getState()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Exemption.class)).thenReturn(Mono.just(exemption));

        // Then
        Mono<Exemption> exemptionMono = exemptionRepository.findByClientCustomerAndState(transaction);
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findByClientCustomerAndState_NullIdPassed_ThrowsException() {
        // Given
        Transaction transactionNull = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionRepository.findByClientCustomerAndState(transactionNull));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void findByClientCustomerAndState_ExemptionDoesNotExist_ReturnsMonoEmpty() {
        // Given
        Query query = Query.query(Criteria
                .where("tenantId").is(tenantId)
                .and("customerId").is(transaction.getCustomerId())
                .and("state.abbreviation").is(transaction.getShippingAddress().getState()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Exemption.class)).thenReturn(Mono.empty());

        // Then
        Mono<Exemption> exemptionMono = exemptionRepository.findByClientCustomerAndState(transaction);
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void save_ExemptionSaved_ExemptionReturned() {
        // Given
        Exemption exemptionNoId = exemption.withId(null);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.save(exemptionNoId)).thenReturn(Mono.just(exemption));

        // Then
        Mono<Exemption> exemptionMono = exemptionRepository.save(exemptionNoId);
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @Test
    void save_NoExemptionReturned_EmptyMonoReturned() {
        // Given
        Exemption exemptionNoId = exemption;

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.save(exemptionNoId)).thenReturn(Mono.empty());

        // Then
        Mono<Exemption> exemptionMono = exemptionRepository.save(exemptionNoId);
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void save_NullIdPassed_ThrowsException() {
        // Given
        Exemption exemptionNull = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionRepository.save(exemptionNull));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void findById_FindsExemption_ReturnsExemption() {
        // Given
        String id = exemption.getId();
        Query query = Query.query(Criteria.where("_id").is(id)
                .and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Exemption.class)).thenReturn(Mono.just(exemption));

        // Then
        Mono<Exemption> exemptionMono = exemptionRepository.findById(id);
        StepVerifier.create(exemptionMono).expectNext(exemption).verifyComplete();
    }

    @Test
    void findById_ExemptionDoesNotExist_ReturnsMonoEmpty() {
        // Given
        String id = exemption.getId();
        Query query = Query.query(Criteria.where("_id").is(id)
                .and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query,Exemption.class)).thenReturn(Mono.empty());

        // Then
        Mono<Exemption> exemptionMono = exemptionRepository.findById(id);
        StepVerifier.create(exemptionMono).verifyComplete();
    }

    @Test
    void findAll_FindsTwoExemptions_ReturnsTwoExemptions() {
        // Given
        Exemption secondExemption = exemption.withId(UUID.randomUUID().toString())
                .withState(new State("NY", "05", "New York"));
        List<Exemption> exemptions = new ArrayList<>() {{
            add(exemption);
            add(secondExemption);
        }};
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Exemption.class)).thenReturn(Flux.fromIterable(exemptions));

        // Then
        Flux<Exemption> exemptionFlux = exemptionRepository.findAll();
        StepVerifier.create(exemptionFlux).expectNext(exemption, secondExemption).verifyComplete();
    }

    @Test
    void findAll_NoExemptionReturned_EmptyFluxReturned() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query,Exemption.class)).thenReturn(Flux.empty());

        // Then
        Flux<Exemption> exemptionFlux = exemptionRepository.findAll();
        StepVerifier.create(exemptionFlux).verifyComplete();
    }

    @Test
    void delete_DeletesExemption_ReturnsDeleteResult() {
        // Given
        String id = exemption.getId();
        Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));
        DeleteResult deleteResult = DeleteResult.acknowledged(1);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.remove(query, Exemption.class)).thenReturn(Mono.just(deleteResult));

        // Then
        Mono<DeleteResult> deleteResultMono = exemptionRepository.delete(id);
        StepVerifier.create(deleteResultMono).expectNext(deleteResult).verifyComplete();
    }

    @Test
    void delete_ExemptionDoesNotExistInDB_ReturnsEmptyMono() {
        // Given
        String id = exemption.getId();
        Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.remove(query, Exemption.class)).thenReturn(Mono.empty());

        // Then
        Mono<DeleteResult> deleteResultMono = exemptionRepository.delete(id);
        StepVerifier.create(deleteResultMono).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionRepository.findById(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void delete_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionRepository.delete(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }
}