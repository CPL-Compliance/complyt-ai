package com.complyt.services.nexus;

import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.repositories.SalesTaxTrackingRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class SalesTaxTrackingServiceImplTest {

    @InjectMocks
    SalesTaxTrackingServiceImpl salesTaxTrackingService;

    @Mock
    SalesTaxTrackingRepository salesTaxTrackingRepository;

    SalesTaxTracking salesTaxTracking;

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false, null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false, null);
        return new SalesTaxTracking(UUID.randomUUID().toString(), state, new ObjectId(),
                true, physicalNexusTracker, economicNexusTracker);
    }

    @BeforeEach
    void setUp() {
        salesTaxTracking = createSalesTaxTracking();
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished() {
        SalesTaxTracking salesTaxTrackingWithNexus = salesTaxTracking
                .withEconomicNexusTracker(new EconomicNexusTracker(true, new Date()));

        return salesTaxTrackingWithNexus;
    }

    @Test
    void findById_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        String id = salesTaxTracking.getId();

        // When
        when(salesTaxTrackingRepository.findById(id)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.findById(id);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findById_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.findById(nullId);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void save_SavesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking salesTaxTrackingNoId = salesTaxTracking.withId(null);

        // When
        when(salesTaxTrackingRepository.save(salesTaxTrackingNoId)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.save(salesTaxTrackingNoId);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void save_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.save(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void findByState_FindsSalesTaxTrackingByState_ReturnsSalesTaxTracking() {
        // Given
        String state = salesTaxTracking.getState().getAbbreviation();

        // When
        when(salesTaxTrackingRepository.findByState(state)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.findByState(state);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findByState_NullStatePassed_ThrowsException() {
        // Given
        String nullState = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.findByState(nullState);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "state is marked non-null but is null");
    }

    @Test
    void findAll_FindsAll_ReturnsAll() {
        // Given
        List<SalesTaxTracking> salesTaxTrackingList = new ArrayList<SalesTaxTracking>() {{
            add(salesTaxTracking);
        }};

        // When
        when(salesTaxTrackingRepository.findAll()).thenReturn(Flux.fromIterable(salesTaxTrackingList));
        Flux<SalesTaxTracking> actualTrackingFlux = salesTaxTrackingService.findAll();

        // Then
        StepVerifier.create(actualTrackingFlux).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void saveWithEconomicQualified_SavesModifiedSalesTaxTracking_ReturnsModifiedSalesTaxTracking() {
        // Given
        SalesTaxTracking salesTaxTrackingWithNexusEstablished = createSalesTaxTrackingWithNexusEstablished();

        // When
        when(salesTaxTrackingRepository.save(any())).thenReturn(Mono.just(salesTaxTrackingWithNexusEstablished));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.saveWithEconomicQualified(salesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void saveWithEconomicQualified_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.saveWithEconomicQualified(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

}
