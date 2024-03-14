package com.complyt.services.nexus;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.nexus.ApplicationDateCreator;
import com.complyt.business.timestamps_injection.SalesTaxTrackingRegisteredDateTimestampsInjector;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.State;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.*;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.RegisteredType;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.repositories.ClientTrackingRepository;
import com.complyt.repositories.NexusStateRuleRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

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
    ClientTrackingRepository clientTrackingRepository;
    @Mock
    NexusStateRuleRepository nexusStateRuleRepository;

    @Mock
    ApplicationDateCreator applicationDateCreator;

    @Mock
    ComplytIdHandler<SalesTaxTracking> complytIdHandler;

    SalesTaxTracking salesTaxTracking;

    ClientTracking clientTracking;

    UnitTestUtilities testUtilities;
    private NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(new ObjectId().toString());
        clientTracking = salesTaxTracking.getClientTracking();
        nexusStateRule = salesTaxTracking.getNexusStateRule();
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
    void save_SavesSalesTaxTrackingWithoutNexusSummary_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking
                .withNexusStateRule(salesTaxTracking.getNexusStateRule().withTimeFrame(TimeFrame.PREVIOUS_TWELVE_MONTHS))
                .withNexusCalculationSummaries(Map.of(LocalDate.now(), new NexusCalculationSummary(3, BigDecimal.valueOf(3405.5))))
                .withId(null);
        SalesTaxTracking salesTaxTrackingWithNoSummary = givenSalesTaxTracking
                .withNexusCalculationSummaries(Map.of());
        SalesTaxTracking savedSalesTaxTracking = salesTaxTrackingWithNoSummary
                .withId("newId");
        SalesTaxTracking expectedSalesTaxTracking = savedSalesTaxTracking
                .withNexusCalculationSummaries(givenSalesTaxTracking.getNexusCalculationSummaries());

        // When
        when(salesTaxTrackingRepository.save(salesTaxTrackingWithNoSummary)).thenReturn(Mono.just(savedSalesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.save(givenSalesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(expectedSalesTaxTracking).verifyComplete();
    }

    @Test
    void save_SavesSalesTaxTrackingWithNexusSummary_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking givenSalesTaxTracking = salesTaxTracking
                .withNexusStateRule(salesTaxTracking.getNexusStateRule().withTimeFrame(TimeFrame.CURRENT_CALENDER_YEAR))
                .withNexusCalculationSummaries(Map.of(LocalDate.now(), new NexusCalculationSummary(3, BigDecimal.valueOf(3405.5))))
                .withId(null);
        SalesTaxTracking savedSalesTaxTracking = givenSalesTaxTracking
                .withId("newId");

        // When
        when(salesTaxTrackingRepository.save(givenSalesTaxTracking)).thenReturn(Mono.just(savedSalesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.save(givenSalesTaxTracking);

        // Then
        StepVerifier.create(actualSalesTaxTracking).expectNext(savedSalesTaxTracking).verifyComplete();
    }

    @Test
    void updateEconomicNexus_UpdatesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given + When
        when(salesTaxTrackingRepository.updateEconomicNexus(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> actualSalesTaxTracking = salesTaxTrackingService.updateEconomicNexus(salesTaxTracking);

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
        when(salesTaxTrackingRepository.findAll(0, salesTaxTrackingList.size())).thenReturn(Flux.fromIterable(salesTaxTrackingList));
        Flux<SalesTaxTracking> actualTrackingFlux = salesTaxTrackingService.findAll(0, salesTaxTrackingList.size());

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
        when(nexusStateRuleRepository.findMostRecentByState(newSalesTaxTracking.getState().getName())).thenReturn(Mono.just(nexusStateRule));
        when(clientTrackingRepository.findClient()).thenReturn(Mono.just(clientTracking));
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
        when(clientTrackingRepository.findClient()).thenReturn(Mono.just(clientTracking));
        when(nexusStateRuleRepository.findMostRecentByState(salesTaxTracking.getState().getName())).thenReturn(Mono.just(nexusStateRule));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.injectDataToNewSalesTaxTracking(salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking.withComplytId(complytId)).verifyComplete();
    }


    @Test
    void injectRegisteredDateToSalesTaxTracking_notNullSalesTaxTracking_ReturnsSalesTaxTrackingWithData() {
        //Given
        SalesTaxTrackingRegisteredDateTimestampsInjector injector = new SalesTaxTrackingRegisteredDateTimestampsInjector(salesTaxTracking);
        SalesTaxTracking salesTaxTrackingWithUpdatedDate = injector.inject();

        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.injectRegisteredDateToSalesTaxTracking(salesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono)
                .expectNextMatches(salesTaxTracking -> {
                    LocalDateTime expectedRegistrationDate = salesTaxTrackingWithUpdatedDate.getRegistrationDate();
                    LocalDateTime actualRegistrationDate = salesTaxTracking.getRegistrationDate();

                    return expectedRegistrationDate.getYear() == actualRegistrationDate.getYear() &&
                            expectedRegistrationDate.getMonthValue() == actualRegistrationDate.getMonthValue() &&
                            expectedRegistrationDate.getDayOfYear() == actualRegistrationDate.getDayOfYear() &&
                            expectedRegistrationDate.getHour() == actualRegistrationDate.getHour();
                })
                .verifyComplete();
    }

    @Test
    void injectRegisteredDateToSalesTaxTracking_NullSalesTaxTracking_ReturnsSalesTaxTrackingWithData() {
        // Given
        SalesTaxTracking salesTaxTracking = null;

        // When & Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.injectRegisteredDateToSalesTaxTracking(salesTaxTracking).block();
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void updateRegisteredDateIfIsRegisteredModified_RegisteredAndDateNull_ReturnsSalesTaxTrackingWithDate() {
        //Given
        SalesTaxTracking salesTaxTrackingUpdated = salesTaxTracking
                .withRegistered(RegisteredType.REGISTERED).withRegistrationDate(null);
        LocalDateTime expectedRegistrationDate = LocalDateTime.now();

        //When
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.updateRegisteredDateIfIsRegisteredModified(salesTaxTrackingUpdated);

        //Then
        StepVerifier.create(salesTaxTrackingMono)
                .expectNextMatches(salesTaxTracking -> {
                    LocalDateTime actualRegistrationDate = salesTaxTracking.getRegistrationDate();
                    return expectedRegistrationDate.getYear() == actualRegistrationDate.getYear() &&
                            expectedRegistrationDate.getMonthValue() == actualRegistrationDate.getMonthValue() &&
                            expectedRegistrationDate.getDayOfYear() == actualRegistrationDate.getDayOfYear() &&
                            expectedRegistrationDate.getHour() == actualRegistrationDate.getHour();
                })
                .verifyComplete();
    }

    @Test
    void updateRegisteredDateIfIsRegisteredModified_RegisteredAndGivenDate_ReturnsSalesTaxTracking() {
        //Given
        LocalDateTime registrationDate = LocalDateTime.now();
        SalesTaxTracking salesTaxTrackingUpdated = salesTaxTracking
                .withRegistered(RegisteredType.REGISTERED).withRegistrationDate(registrationDate);

        //When
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.updateRegisteredDateIfIsRegisteredModified(salesTaxTrackingUpdated);

        //Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingUpdated).verifyComplete();
    }

    @Test
    void updateRegisteredDateIfIsRegisteredModified_NonRegisteredAndGivenDate_ReturnsSalesTaxTracking() {
        //Given
        LocalDateTime registrationDate = LocalDateTime.now();
        SalesTaxTracking salesTaxTrackingUpdated = salesTaxTracking
                .withRegistered(null).withRegistrationDate(registrationDate);

        //When
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.updateRegisteredDateIfIsRegisteredModified(salesTaxTrackingUpdated);

        //Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingUpdated).verifyComplete();
    }

    @Test
    void insertSummariesFromOriginal_OriginalHaveSummaries_ReturnsNewWithSummaries() {
        // Given
        SalesTaxTracking originalSalesTaxTracking = salesTaxTracking
                .withNexusCalculationSummaries(Map.of(LocalDate.now(),
                        new NexusCalculationSummary(1, BigDecimal.valueOf(15000))))
                .withTransactionNexusSummaries(Map.of(UUID.randomUUID(),
                        new TransactionNexusSummary(BigDecimal.valueOf(15000), LocalDateTime.now(), TransactionType.INVOICE)));

        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComment("new");

        SalesTaxTracking expectedSalesTaxTracking = newSalesTaxTracking
                .withNexusCalculationSummaries(originalSalesTaxTracking.getNexusCalculationSummaries())
                .withTransactionNexusSummaries(originalSalesTaxTracking.getTransactionNexusSummaries());

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.insertSummariesFromOriginal(newSalesTaxTracking, originalSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(expectedSalesTaxTracking).verifyComplete();
    }

    @Test
    void insertSummariesFromOriginal_OriginalDoesNotHaveSummaries_ReturnsNewWithEmptyMaps() {
        // Given
        SalesTaxTracking originalSalesTaxTracking = salesTaxTracking
                .withNexusCalculationSummaries(null)
                .withTransactionNexusSummaries(null);

        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComment("new");

        SalesTaxTracking expectedSalesTaxTracking = newSalesTaxTracking
                .withNexusCalculationSummaries(new HashMap<>())
                .withTransactionNexusSummaries(new HashMap<>());

        // When
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.insertSummariesFromOriginal(newSalesTaxTracking, originalSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(expectedSalesTaxTracking).verifyComplete();
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
    void handleSalesTaxTrackingAfterTransactionCalculated_SalesTaxTrackingWithEconomicNexusEstablished_UpdatesSalesTaxTracking() {
        // Given
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(true, LocalDateTime.now());
        SalesTaxTracking salesTaxTrackingWithEconomicNexusEstablished = salesTaxTracking.withEconomicNexusTracker(economicNexusTracker);

        // When
        when(salesTaxTrackingRepository.updateEconomicNexus(salesTaxTrackingWithEconomicNexusEstablished)).thenReturn(Mono.just(salesTaxTrackingWithEconomicNexusEstablished));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(salesTaxTrackingWithEconomicNexusEstablished);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithEconomicNexusEstablished).verifyComplete();
    }

    @Test
    void handleSalesTaxTrackingAfterTransactionCalculated_SalesTaxTrackingWithEconomicNexusNotEstablished_SavesSalesTaxTracking() {
        // Given
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false, null);
        SalesTaxTracking salesTaxTrackingWithEconomicNexusNotEstablished = salesTaxTracking.withEconomicNexusTracker(economicNexusTracker);

        // When
        when(salesTaxTrackingRepository.save(salesTaxTrackingWithEconomicNexusNotEstablished)).thenReturn(Mono.just(salesTaxTrackingWithEconomicNexusNotEstablished));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(salesTaxTrackingWithEconomicNexusNotEstablished);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithEconomicNexusNotEstablished).verifyComplete();
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
    void upsertWithoutNexusSummaryIfNeeded_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.upsertWithoutNexusSummaryIfNeeded(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void addClientAndStateDetails_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.addClientAndStateDetails(nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

    @Test
    void insertSummariesFromOriginal_NullNewSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.insertSummariesFromOriginal(nullSalesTaxTracking, salesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "checkedSalesTaxTracking is marked non-null but is null");
    }

    @Test
    void insertSummariesFromOriginal_NullOriginalSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.insertSummariesFromOriginal(salesTaxTracking, nullSalesTaxTracking);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "originalSalesTaxTracking is marked non-null but is null");
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

    @Test
    void handleSalesTaxTrackingAfterTransactionCalculated_NullSalesTaxTracking_ThrowsNullPointerException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxTrackingService.handleSalesTaxTrackingAfterTransactionCalculated(nullSalesTaxTracking);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }
}
