package com.complyt.repositories;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
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
import testUtils.DomainObjectStub;

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
    Exemption exemption;
    Transaction transaction;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        MockitoAnnotations.openMocks(this);
        exemption = domainObjectStub.createExemption(UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void findByClientCustomerAndState_FindsExemption_ReturnsExemption() {
        // Given
        Query query = Query.query(Criteria
                .where("tenantId").is(transaction.getTenantId())
                .and("customerId").is(transaction.getCustomerId())
                .and("state.abbreviation").is(transaction.getShippingAddress().getState()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
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
                .where("tenantId").is(transaction.getTenantId())
                .and("customerId").is(transaction.getCustomerId())
                .and("state.abbreviation").is(transaction.getShippingAddress().getState()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
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
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
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
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
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
                .and("tenantId").is(transaction.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
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
                .and("tenantId").is(transaction.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, Exemption.class)).thenReturn(Mono.empty());

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
        Query query = Query.query(Criteria.where("tenantId").is(transaction.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.find(query, Exemption.class)).thenReturn(Flux.fromIterable(exemptions));

        // Then
        Flux<Exemption> exemptionFlux = exemptionRepository.findAll();
        StepVerifier.create(exemptionFlux).expectNext(exemption, secondExemption).verifyComplete();
    }

    @Test
    void findAll_NoExemptionReturned_EmptyFluxReturned() {
        // Given
        Query query = Query.query(Criteria.where("tenantId").is(transaction.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.find(query, Exemption.class)).thenReturn(Flux.empty());

        // Then
        Flux<Exemption> exemptionFlux = exemptionRepository.findAll();
        StepVerifier.create(exemptionFlux).verifyComplete();
    }

    @Test
    void delete_DeletesExemption_ReturnsDeleteResult() {
        // Given
        UUID id = exemption.getComplytId();
        Query query = Query.query(Criteria.where("complytId").is(id).and("tenantId").is(transaction.getTenantId()));
        DeleteResult deleteResult = DeleteResult.acknowledged(1);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.remove(query, Exemption.class)).thenReturn(Mono.just(deleteResult));

        // Then
        Mono<DeleteResult> deleteResultMono = exemptionRepository.delete(id);
        StepVerifier.create(deleteResultMono).expectNext(deleteResult).verifyComplete();
    }

    @Test
    void delete_ExemptionDoesNotExistInDB_ReturnsEmptyMono() {
        // Given
        UUID id = exemption.getComplytId();
        Query query = Query.query(Criteria.where("complytId").is(id).and("tenantId").is(transaction.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(transaction.getTenantId()));
        when(reactiveMongoTemplate.remove(query, Exemption.class)).thenReturn(Mono.empty());

        // Then
        Mono<DeleteResult> deleteResultMono = exemptionRepository.delete(id);
        StepVerifier.create(deleteResultMono).verifyComplete();
    }

    @Test
    void findByComplytId_IdDoesNotExist_ReturnsEmpty() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(exemption.getTenantId()));
        when(tenantResolver.resolve()).thenReturn(Mono.just(exemption.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, Exemption.class)).thenReturn(Mono.empty());

        // Then
        Mono<Exemption> monoExemption = exemptionRepository.findByComplytId(complytId);
        StepVerifier.create(monoExemption).verifyComplete();
    }

    @Test
    void findByComplytId_IdExist_ReturnsExemption() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(exemption.getTenantId()));
        when(tenantResolver.resolve()).thenReturn(Mono.just(exemption.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, Exemption.class)).thenReturn(Mono.just(exemption.withComplytId(complytId)));

        // Then
        Mono<Exemption> monoExemption = exemptionRepository.findByComplytId(complytId);
        StepVerifier.create(monoExemption).expectNext(exemption.withComplytId(complytId)).verifyComplete();
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
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionRepository.delete(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }
}