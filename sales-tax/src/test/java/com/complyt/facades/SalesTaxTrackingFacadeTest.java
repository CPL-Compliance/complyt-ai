package com.complyt.facades;

import com.complyt.domain.State;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.services.nexus.SalesTaxTrackingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

@ExtendWith(MockitoExtension.class)
public class SalesTaxTrackingFacadeTest {

    @InjectMocks
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    @Mock
    SalesTaxTrackingService salesTaxTrackingService;

    private SalesTaxTracking salesTaxTracking;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxTracking = domainObjectStub.createSalesTaxTracking(UUID.randomUUID().toString());
    }

    @Test
    void save_SavesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null);
        SalesTaxTracking salesTaxTrackingWithId = newSalesTaxTracking.withComplytId(salesTaxTracking.getComplytId());

        // When
        when(salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(newSalesTaxTracking)).thenReturn(Mono.just(newSalesTaxTracking));
        when(salesTaxTrackingService.injectDataToNewSalesTaxTracking(newSalesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));
        when(salesTaxTrackingService.save(salesTaxTrackingWithId)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.save(newSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void update_UpdatesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        String state = salesTaxTracking.getState().getName();
        SalesTaxTracking salesTaxTrackingWithId = salesTaxTracking.withId(UUID.randomUUID().toString());

        // When
        when(salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(salesTaxTracking, salesTaxTrackingWithId)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.update(salesTaxTracking, state)).thenReturn(Mono.just(salesTaxTrackingWithId));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.update(salesTaxTracking, salesTaxTrackingWithId, state);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithId).verifyComplete();
    }

    @Test
    void findByState_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        String state = salesTaxTracking.getState().getName();

        // When
        when(salesTaxTrackingService.findByState(state)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.findByState(state);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findAll_FindsAll_ReturnsAll() {
        // Given
        SalesTaxTracking secondSalesTaxTracking = salesTaxTracking
                .withState(new State("NY", "05", "New York"));

        List<SalesTaxTracking> salesTaxTrackingList = new ArrayList<>() {{
            add(salesTaxTracking);
            add(secondSalesTaxTracking);
        }};

        // When
        when(salesTaxTrackingService.findAll()).thenReturn(Flux.fromIterable(salesTaxTrackingList));
        Flux<SalesTaxTracking> salesTaxTrackingFlux = salesTaxTrackingFacade.findAll();

        // Then
        StepVerifier.create(salesTaxTrackingFlux)
                .expectNext(salesTaxTracking)
                .expectNext(secondSalesTaxTracking)
                .expectNextCount(0)
                .verifyComplete();

    }

    @Test
    void getByComplytId_SalesTaxTrackingExists_ReturnsSalesTaxTracking() {
        // Given
        UUID complytId = salesTaxTracking.getComplytId();

        // When
        when(salesTaxTrackingService.findByComplytId(complytId)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.findByComplytId(complytId);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void update_NullStatePassed_ThrowsException() {
        // Given
        String nullState = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.update(salesTaxTracking, salesTaxTracking, nullState));

        // Then
        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");

    }

    @Test
    void update_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;
        String state = salesTaxTracking.getState().getName();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.update(nullSalesTaxTracking, salesTaxTracking, state));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void save_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.save(nullSalesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void findByState_NullStatePassed_ThrowsException() {
        // Given
        String nullState = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.findByState(nullState));

        // Then
        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");
    }

    @Test
    void findByComplytId_NullIdPassed_ThrowsException() {
        // Given
        UUID nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.findByComplytId(nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

    @Test
    void update_NullOriginalSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;
        String state = salesTaxTracking.getState().getName();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.update(salesTaxTracking, nullSalesTaxTracking, state));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalSalesTaxTracking is marked non-null but is null");
    }

}
