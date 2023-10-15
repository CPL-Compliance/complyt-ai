package com.complyt.services.nexus;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.domain.State;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.repositories.SalesTaxTrackingRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesTaxTrackingServiceImplTest {

    @InjectMocks
    SalesTaxTrackingServiceImpl salesTaxTrackingService;

    @Mock
    SalesTaxTrackingRepository salesTaxTrackingRepository;

    @Mock
    ApplicationDateCreator applicationDateCreator;

    @Mock
    ComplytIdHandler<SalesTaxTracking> complytIdHandler;

    SalesTaxTracking salesTaxTracking;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString());
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished() {
        return salesTaxTracking
                .withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<TaxableCategory>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<CustomerType>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(new BigDecimal(1000), 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.CURRENT_CALENDER_YEAR, nexusThreshold, LocalDateTime.now());
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
        NexusStateRule stateRule = createNexusStateRule();
        LocalDateTime referenceDate = LocalDateTime.now();

        // When
        when(salesTaxTrackingRepository.save(any())).thenReturn(Mono.just(salesTaxTrackingWithNexusEstablished));
        when(applicationDateCreator.create(stateRule.timeFrame(), referenceDate)).thenReturn(LocalDateTime.now());
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.saveWithEconomicQualified(salesTaxTracking, stateRule, referenceDate);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(salesTaxTrackingWithNexusEstablished).verifyComplete();
    }

    @Test
    void update_SalesTaxTrackingUpdated_SalesTaxTrackingReturned() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.
                withEconomicNexusTracker(new EconomicNexusTracker(true, LocalDateTime.now()));
        String state = newSalesTaxTracking.getState().getName();

        // When
        when(salesTaxTrackingRepository.findByState(state)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingRepository.save(newSalesTaxTracking)).thenReturn(Mono.just(newSalesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.update(newSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(newSalesTaxTracking).verifyComplete();
    }

    @Test
    void checkSalesTaxTrackingNotHavingComplytId_DoesHaveComplytId_ThrowsException() {
        // Given When
        when(complytIdHandler.checkNewDontHaveComplytId(salesTaxTracking)).thenReturn(Mono.error(new NotFoundException("cannot insert new salesTaxTracking with complyt id")));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectErrorMessage("cannot insert new salesTaxTracking with complyt id").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdMotEquals_ThrowsExceptions() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(UUID.randomUUID());

        // When
        when(complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newSalesTaxTracking, salesTaxTracking)).thenReturn(Mono.error(new NotFoundException("complyt ids of modified and original salesTaxTrackings are not equal")));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(newSalesTaxTracking, salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectErrorMessage("complyt ids of modified and original salesTaxTrackings are not equal").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_DoesNotHaveComplytId_ReturnsNewSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null);

        // When
        when(complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newSalesTaxTracking, salesTaxTracking)).thenReturn(Mono.just(newSalesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(newSalesTaxTracking, salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(newSalesTaxTracking).verifyComplete();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdAreEquals_ReturnsNewSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(salesTaxTracking.getComplytId());

        // When
        when(complytIdHandler.checkComplytIdOfUpdatedEqualsToOld(newSalesTaxTracking, salesTaxTracking)).thenReturn(Mono.just(newSalesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(newSalesTaxTracking, salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(newSalesTaxTracking).verifyComplete();
    }

    @Test
    void findByComplytId_complytIdExists_ReturnsSalesTaxTracking() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        when(salesTaxTrackingRepository.findByComplytId(complytId)).thenReturn(Mono.just(salesTaxTracking.withComplytId(complytId)));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.findByComplytId(complytId);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void injectDataToNewSalesTaxTracking_notNullSalesTaxTracking_ReturnsSalesTaxTrackingWithData() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        when(complytIdHandler.insertComplytIdToNew(salesTaxTracking)).thenReturn(salesTaxTracking.withComplytId(complytId));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.injectDataToNewSalesTaxTracking(salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void update_SalesTaxTrackingNotFoundByState_ThrowsNotFoundException() {
        // Given
        String state = salesTaxTracking.getState().getName();

        // When
        when(salesTaxTrackingRepository.findByState(state)).thenReturn(Mono.empty());
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.update(salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectError().verify();
    }

    @Test
    void update_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.update(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void saveWithEconomicQualified_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;
        NexusStateRule nexusStateRule = createNexusStateRule();
        LocalDateTime referenceDate = LocalDateTime.now();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.saveWithEconomicQualified(nullSalesTaxTracking, nexusStateRule, referenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void saveWithEconomicQualified_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;
        LocalDateTime referenceDate = LocalDateTime.now();

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.saveWithEconomicQualified(salesTaxTracking, nullNexusStateRule, referenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "stateRule is marked non-null but is null");
    }

    @Test
    void saveWithEconomicQualified_NullReferenceDatePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = createNexusStateRule();
        LocalDateTime nullReferenceDate = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.saveWithEconomicQualified(salesTaxTracking, nullNexusStateRule, nullReferenceDate);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "referenceDate is marked non-null but is null");
    }

    @Test
    void checkCustomerNotHavingComplytId_NullGiven_ThrowsNullPointerException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(nullSalesTaxTracking);
        });

        assertEquals(nullPointerException.getMessage(), "newSalesTaxTracking is marked non-null but is null");
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_NullModifiedSalesTaxTracking_ThrowsNullPointerException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(nullSalesTaxTracking, salesTaxTracking);
        });

        assertEquals(nullPointerException.getMessage(), "modifiedSalesTaxTracking is marked non-null but is null");
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_NullOriginalSalesTaxTracking_ThrowsNullPointerException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(salesTaxTracking, nullSalesTaxTracking);
        });

        assertEquals(nullPointerException.getMessage(), "originalSalesTaxTracking is marked non-null but is null");
    }

    @Test
    void injectDataToNewSalesTaxTracking_NullSalesTaxTracking_ThrowsNullPointerException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.injectDataToNewSalesTaxTracking(nullSalesTaxTracking);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void findByComplytId_NullSalesTaxTracking_ThrowsNullPointerException() {
        // Given
        UUID nullComplytId = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.findByComplytId(nullComplytId);
        });

        assertEquals(nullPointerException.getMessage(), "complytId is marked non-null but is null");
    }

}
