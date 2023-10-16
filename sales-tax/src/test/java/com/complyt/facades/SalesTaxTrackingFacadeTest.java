package com.complyt.facades;

import com.complyt.domain.State;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
<<<<<<< HEAD
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.transaction.Transaction;
import com.complyt.services.CustomerService;
import com.complyt.services.TransactionService;
=======
>>>>>>> 91047832 (added summaryDto and mapper)
import com.complyt.services.nexus.NexusService;
import com.complyt.services.nexus.SalesTaxTrackingService;
import com.complyt.utils.factory.DateRange;
import com.complyt.utils.query.DateRangeStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SalesTaxTrackingFacadeTest {

    @InjectMocks
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    @Mock
    SalesTaxTrackingService salesTaxTrackingService;
<<<<<<< HEAD
    @Mock
    TransactionService transactionService;
    @Mock
    CustomerService customerService;
    @Mock
    NexusService nexusService;
    UnitTestUtilities testUtilities;
=======

    @Mock
    NexusService nexusService;

>>>>>>> 91047832 (added summaryDto and mapper)
    private SalesTaxTracking salesTaxTracking;

    private final DateRange dateRange = new DateRangeStrategy(TimeFrame.PREVIOUS_TWELVE_MONTHS, LocalDateTime.now(), LocalDateTime.now()).getDateRange();

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
    }

    @Test
    void save_SavesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking newSalesTaxTracking = salesTaxTracking.withComplytId(null).withId(null).withTenantId(null);
        SalesTaxTracking salesTaxTrackingWithId = newSalesTaxTracking.withComplytId(salesTaxTracking.getComplytId());

        // When

        when(salesTaxTrackingService.checkSalesTaxTrackingNotHavingComplytId(newSalesTaxTracking)).thenReturn(Mono.just(newSalesTaxTracking));
        when(salesTaxTrackingService.addClientAndStateDetails(newSalesTaxTracking)).thenReturn(Mono.just(newSalesTaxTracking));
        when(salesTaxTrackingService.injectDataToNewSalesTaxTracking(newSalesTaxTracking)).thenReturn(Mono.just(newSalesTaxTracking));
        when(salesTaxTrackingService.save(newSalesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));
        when(nexusService.getNexusSummaryDate(eq(salesTaxTrackingWithId), any())).thenReturn(Mono.just(dateRange));
        when(nexusService.recalculationOfNexusSummaryIfRequired(eq(salesTaxTrackingWithId), any())).thenReturn(Mono.just(salesTaxTrackingWithId));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.save(newSalesTaxTracking);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithId).verifyComplete();
    }

    @Test
    void update_UpdatesSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        SalesTaxTracking salesTaxTrackingWithId = salesTaxTracking.withId(UUID.randomUUID().toString());

        // When
        when(salesTaxTrackingService.checkComplytIdOfModifiedEqualsToOriginal(salesTaxTracking, salesTaxTrackingWithId)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.addClientAndStateDetails(salesTaxTracking)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.insertSummariesFromOriginal(salesTaxTracking, salesTaxTrackingWithId)).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.update(salesTaxTracking)).thenReturn(Mono.just(salesTaxTrackingWithId));
        when(nexusService.getNexusSummaryDate(eq(salesTaxTrackingWithId), any())).thenReturn(Mono.just(dateRange));
        when(nexusService.recalculationOfNexusSummaryIfRequired(eq(salesTaxTrackingWithId), any())).thenReturn(Mono.just(salesTaxTrackingWithId));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.update(salesTaxTracking, salesTaxTrackingWithId);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithId).verifyComplete();
    }

    @Test
    void refreshNexusSummary_EverythingExists_ReturnsSalesTaxTrackingWithNewSummary() {
        // Given
        SalesTaxTracking salesTaxTrackingWithId = salesTaxTracking.withId(UUID.randomUUID().toString());
        LocalDate referenceDate = LocalDate.now();
        Transaction transaction = testUtilities.createTransaction("1234");
        Customer customer = testUtilities.createCustomer("1111");
        List<Transaction> transactionsWithCustomers = List.of(transaction.withCustomer(customer));
        SalesTaxTracking salesTaxTrackingWithSummary = salesTaxTrackingWithId.withTransactionNexusSummaries(Map.of(
                        transaction.getComplytId(),
                        new TransactionNexusSummary(BigDecimal.valueOf(42),
                                transaction.getExternalTimestamps().getCreatedDate(),
                                transaction.getTransactionType())))
                .withNexusCalculationSummaries(Map.of(
                        referenceDate,
                        new NexusCalculationSummary(1, BigDecimal.valueOf(42))));
        Query query = new Query();

        // When
        when(salesTaxTrackingService.findByState(salesTaxTracking.getState().getName())).thenReturn(Mono.just(salesTaxTrackingWithId));
        when(salesTaxTrackingService.addClientAndStateDetails(salesTaxTrackingWithId)).thenReturn(Mono.just(salesTaxTrackingWithId));
        when(nexusService.getTransactionsQueryByNexusCalculation(salesTaxTrackingWithId.getNexusStateRule(), salesTaxTrackingWithId.getClientTracking(), referenceDate)).thenReturn(Mono.just(query));
        when(transactionService.getTransactionsByQuery(query)).thenReturn(Flux.just(transaction));
        when(customerService.findByComplytId(transaction.getCustomerId())).thenReturn(Mono.just(customer));
        when(nexusService.refreshNexusSummary(salesTaxTrackingWithId, transactionsWithCustomers, referenceDate)).thenReturn(Mono.just(salesTaxTrackingWithSummary));
        when(salesTaxTrackingService.update(salesTaxTrackingWithSummary)).thenReturn(Mono.just(salesTaxTrackingWithSummary));

        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.refreshNexusSummary(salesTaxTracking.getState().getName(), referenceDate);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithSummary).verifyComplete();
    }

    @Test
    void findByState_FindsSalesTaxTracking_ReturnsSalesTaxTracking() {
        // Given
        String state = salesTaxTracking.getState().getName();

        // When
        when(nexusService.getNexusSummaryDate(eq(salesTaxTracking), any())).thenReturn(Mono.just(dateRange));
        when(nexusService.recalculationOfNexusSummaryIfRequired(eq(salesTaxTracking), any())).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.findByState(state)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.findByState(state);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void findByState_FindsSalesTaxTrackingWithNoStateRule_ReturnsSalesTaxTrackingWithoutRecalculationOfSummary() {
        // Given
        String state = salesTaxTracking.getState().getName();
        SalesTaxTracking salesTaxTrackingWithNoStateRule = salesTaxTracking.withNexusStateRule(null);

        // When
        when(salesTaxTrackingService.findByState(state)).thenReturn(Mono.just(salesTaxTrackingWithNoStateRule));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.findByState(state);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTrackingWithNoStateRule).verifyComplete();
    }

    @Test
    void findAll_FindsAll_ReturnsAll() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        SalesTaxTracking secondSalesTaxTracking = salesTaxTracking
                .withState(new State("NY", "05", "New York"));

        List<SalesTaxTracking> salesTaxTrackingList = new ArrayList<>() {{
            add(salesTaxTracking);
            add(secondSalesTaxTracking);
        }};

        // When
        when(salesTaxTrackingService.findAll()).thenReturn(Flux.fromIterable(salesTaxTrackingList));
        when(nexusService.getNexusSummaryDate(eq(salesTaxTracking), any())).thenReturn(Mono.just(dateRange));
        when(nexusService.getNexusSummaryDate(eq(secondSalesTaxTracking), any())).thenReturn(Mono.just(dateRange));
        when(nexusService.recalculationOfNexusSummaryIfRequired(eq(salesTaxTracking), any())).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.recalculationOfNexusSummaryIfRequired(eq(secondSalesTaxTracking), any())).thenReturn(Mono.just(secondSalesTaxTracking));
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
        when(nexusService.getNexusSummaryDate(eq(salesTaxTracking), any())).thenReturn(Mono.just(dateRange));
        when(nexusService.recalculationOfNexusSummaryIfRequired(eq(salesTaxTracking), any())).thenReturn(Mono.just(salesTaxTracking));
        when(salesTaxTrackingService.findByComplytId(complytId)).thenReturn(Mono.just(salesTaxTracking));
        Mono<SalesTaxTracking> salesTaxTrackingMono = salesTaxTrackingFacade.findByComplytId(complytId);

        // Then
        StepVerifier.create(salesTaxTrackingMono).expectNext(salesTaxTracking).verifyComplete();
    }

    @Test
    void update_NullSalesTaxTrackingPassed_ThrowsException() {
        // Given
        SalesTaxTracking nullSalesTaxTracking = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.update(nullSalesTaxTracking, salesTaxTracking));

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

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxTrackingFacade.update(salesTaxTracking, nullSalesTaxTracking));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalSalesTaxTracking is marked non-null but is null");
    }

}
