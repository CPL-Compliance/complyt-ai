package com.complyt.repositories;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.security.TenantResolver;
import com.complyt.utils.query.SalesTaxTrackingUpdateQueryBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
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

    @Mock
    SalesTaxTrackingUpdateQueryBuilder updateQueryBuilder;

    SalesTaxTracking salesTaxTracking;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
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

    @Test
    void updateEconomicNexus_UpdatesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        Query query = new Query(Criteria.where("_id").is(salesTaxTracking.getId()));
        Update update = testUtilities.buildSalesTaxTrackingUpdate(salesTaxTracking);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(updateQueryBuilder.build(salesTaxTracking)).thenReturn(update);
        when(reactiveMongoTemplate.findAndModify(query, update, SalesTaxTracking.class)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingRepository.updateEconomicNexus(salesTaxTracking);

        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void updateEconomicNexus_UpdatesSalesTaxTrackingOfPreviousTwelveMonths_ReturnsSalesTaxTracking() {
        // Given
        Query query = new Query(Criteria.where("_id").is(salesTaxTracking.getId()));
        Update update = testUtilities.buildSalesTaxTrackingUpdateOfPreviousTwelveMonths(salesTaxTracking);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(updateQueryBuilder.build(salesTaxTracking)).thenReturn(update);
        when(reactiveMongoTemplate.findAndModify(query, update, SalesTaxTracking.class)).thenReturn(Mono.just(salesTaxTracking));

        // Then
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingRepository.updateEconomicNexus(salesTaxTracking);

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
    void findByComplytId_IdDoesNotExist_ReturnsEmpty() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(salesTaxTracking.getTenantId()));
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class)).thenReturn(Mono.empty());

        // Then
        Mono<SalesTaxTracking> monoSalesTaxTracking = salesTaxTrackingRepository.findByComplytId(complytId);
        StepVerifier.create(monoSalesTaxTracking).verifyComplete();
    }


    @Test
    void findAll_FindsTwoSalesTaxTrackingPageZeroLimitEqSize_ReturnsTwoExemptions() {
        // Given
        int page = 1;
        int size = 2;
        int calculatedOffset = 0;

        SalesTaxTracking secondSTR = salesTaxTracking.withComplytId(UUID.randomUUID()).withId(UUID.randomUUID().toString());

        List<SalesTaxTracking> salesTaxTracking = new ArrayList<>() {{
            add(SalesTaxTrackingRepositoryTest.this.salesTaxTracking);
            add(secondSTR);
        }};

        Query query = Query.query(Criteria.where("tenantId").is(this.salesTaxTracking.getTenantId()))
                .skip(calculatedOffset)
                .limit(size);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(this.salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.find(eq(query), eq(SalesTaxTracking.class)))
                .thenReturn(Flux.fromIterable(salesTaxTracking));
        // Then
        Flux<SalesTaxTracking> exemptionFlux = salesTaxTrackingRepository.findAll(page, size);
        StepVerifier.create(exemptionFlux).expectNext(this.salesTaxTracking, secondSTR).verifyComplete();
    }

    @Test
    void findAll_FindsTwoSalesTaxTracking_ReturnsTwoSalesTaxTracking() {
        // given
        int page = 2;
        int size = 2;
        int calculatedOffset = (page - 1) * size;

        List<SalesTaxTracking> exemptionList = IntStream.range(0, 4)
                .mapToObj(i -> salesTaxTracking.withComplytId(UUID.randomUUID()).withId(UUID.randomUUID().toString()))
                .collect(Collectors.toList());

        // Creating the expected list (list of 2-4 exemptions)
        List<SalesTaxTracking> expectedList = exemptionList.subList(calculatedOffset, Math.min(calculatedOffset + size, exemptionList.size()));

        Query query = Query.query(Criteria.where("tenantId").is(salesTaxTracking.getTenantId()))
                .skip(calculatedOffset)
                .limit(size);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.find(eq(query), eq(SalesTaxTracking.class))).thenReturn(Flux.fromIterable(expectedList));

        // Then
        Flux<SalesTaxTracking> exemptionFlux = salesTaxTrackingRepository.findAll(page, size);
        StepVerifier.create(exemptionFlux).expectNextSequence(expectedList).verifyComplete();
    }

    @Test
    void findAll_NoExemptionReturnedOffsetZeroLimitEqSize_EmptyFluxReturned() {
        // Given
        int page = 1;
        int size = 1;
        int CalculatedOffset = 0;

        Query query = Query.query(Criteria.where("tenantId").is(salesTaxTracking.getTenantId()))
                .skip(CalculatedOffset)
                .limit(size);
        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.find(eq(query), eq(SalesTaxTracking.class))).thenReturn(Flux.empty());

        // Then
        Flux<SalesTaxTracking> exemptionFlux = salesTaxTrackingRepository.findAll(page, size);
        StepVerifier.create(exemptionFlux).verifyComplete();
    }


    @Test
    void findAll_SalesTaxTrackingByOffsetAndLimit_ExpectingChunkOfSalesTaxTracking() {
        // Given
        int page = 2;
        int size = 2;
        int calculatedOffset = (page - 1) * size;

        List<SalesTaxTracking> salesTaxTrackingList = IntStream.range(0, 4)
                .mapToObj(i -> salesTaxTracking.withComplytId(UUID.randomUUID()).withId(UUID.randomUUID().toString()))
                .collect(Collectors.toList());

        // Creating the expected list (list of 2-4 sales tax tracking objects)
        List<SalesTaxTracking> expectedList = salesTaxTrackingList.subList(page, Math.min(page + size, salesTaxTrackingList.size()));

        Query query = Query.query(Criteria.where("tenantId").is(salesTaxTracking.getTenantId()))
                .skip(calculatedOffset)
                .limit(size);

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.find(eq(query), eq(SalesTaxTracking.class))).thenReturn(Flux.fromIterable(expectedList));

        // Then
        Flux<SalesTaxTracking> salesTaxTrackingFlux = salesTaxTrackingRepository.findAll(page, size);
        StepVerifier.create(salesTaxTrackingFlux).expectNextSequence(expectedList).verifyComplete();
    }


    @Test
    void findByComplytId_IdExist_ReturnsSalesTaxTracking() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(salesTaxTracking.getTenantId()));
        when(tenantResolver.resolve()).thenReturn(Mono.just(salesTaxTracking.getTenantId()));
        when(reactiveMongoTemplate.findOne(query, SalesTaxTracking.class)).thenReturn(Mono.just(salesTaxTracking.withComplytId(complytId)));

        // Then
        Mono<SalesTaxTracking> monoSalesTaxTracking = salesTaxTrackingRepository.findByComplytId(complytId);
        StepVerifier.create(monoSalesTaxTracking).expectNext(salesTaxTracking.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void findByComplytId_NullComplytIdPassed_ThrowsException() {
        // Given
        UUID nullComplytId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingRepository.findByComplytId(nullComplytId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void updateEconomicNexus_NullSalesTaxTracking_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingRepository.updateEconomicNexus(nullSalesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }
}