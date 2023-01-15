package com.complyt.repositories;

import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.security.TenantResolver;
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
public class SalesTaxTrackingRepositoryTest {

    @InjectMocks
    SalesTaxTrackingRepository salesTaxTrackingRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    TenantResolver tenantResolver;

    SalesTaxTracking salesTaxTracking;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        MockitoAnnotations.openMocks(this);
        salesTaxTracking = domainObjectStub.createSalesTaxTracking(UUID.randomUUID().toString());
    }

    @Test
    void findByState_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        String state = salesTaxTracking.getState().getAbbreviation();
        Criteria stateSearchCriteria = new Criteria()
                .orOperator(Criteria.where("state.abbreviation").is(state),
                        Criteria.where("state.name").is(state));

        Query query = Query.query(stateSearchCriteria.and("tenantId").is(salesTaxTracking.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingRepository.findByState(state);

        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findByState_NullState_ThrowsException() {
        // Given
        String nullStateAbbreviation = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingRepository.findByState(nullStateAbbreviation));

        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");
    }

    @Test
    void save_SavesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking salesTaxTrackingNoId = salesTaxTracking.withId(null);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.save(salesTaxTrackingNoId)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingRepository.save(salesTaxTrackingNoId);

        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void save_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingRepository.save(nullSalesTaxTracking));

        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void findById_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {
        // given
        String id = salesTaxTracking.getId();
        Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(salesTaxTracking.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingRepository.findById(id);

        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findAll_FindsTwoSalesTaxTracking_ReturnsTwoSalesTaxTracking() {
        // given
        List<SalesTaxTracking> salesTaxTrackingList = new ArrayList<>() {{
            add(salesTaxTracking);

        }};

        Query query = Query.query(Criteria.where("tenantId").is(salesTaxTracking.getTenantId()));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.find(query, SalesTaxTracking.class)).thenReturn(Flux.fromIterable(salesTaxTrackingList));

        // Then
        Flux<SalesTaxTracking> salesTaxTrackingFlux = salesTaxTrackingRepository.findAll();

        StepVerifier.create(salesTaxTrackingFlux).expectNext(salesTaxTracking).verifyComplete();
    }
}