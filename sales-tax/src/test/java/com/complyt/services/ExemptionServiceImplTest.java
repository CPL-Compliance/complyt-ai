package com.complyt.services;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.repositories.ExemptionRepository;
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
import java.util.*;

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
    ObjectId clientId = new ObjectId();

    @BeforeEach
    void setUp() {
        customer = createCustomer();
        transaction = createTransaction();
        exemption = createExemption();
    }

    private Customer createCustomer() {
        return new Customer(customerId.toString(), UUID.randomUUID().toString(), "name", null, clientId, CustomerType.RETAIL, null);
    }

    private Exemption createExemption() {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");

        return new Exemption(UUID.randomUUID().toString(), new ObjectId(), new ObjectId(),
                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
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
        TimeStamps externalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, clientId, null, externalTimeStamps, TransactionType.INVOICE);
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
        StepVerifier.create(exemptionMono).expectNext(exemption);
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
        StepVerifier.create(exemptionFlux).expectNext(exemption, secondExemption);
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
    void isFullyExempted_NoExemptionStatesToCustomer_ReturnsFalse() {
        // Given

        // When
        when(exemptionRepository.findByClientCustomerAndState(transaction)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transaction);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_StateDoesNotExistInCustomersExemptionsList_ReturnsFalse() {
        // Given
        Map<String, ExemptionType> exemptionStates = new HashMap<String, ExemptionType>() {{
            put("NY", ExemptionType.PARTIALLY);
        }};
        Customer newCustomer = customer.withExemptionsStates(exemptionStates);
        Transaction transactionWithNewCustomer = transaction.withCustomer(newCustomer);

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithNewCustomer)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithNewCustomer);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_CustomerHasPartiallyExemptionInState_ReturnsFalse() {
        // Given
        Map<String, ExemptionType> exemptionStates = new HashMap<String, ExemptionType>() {{
            put("CA", ExemptionType.PARTIALLY);
        }};
        Customer newCustomer = customer.withExemptionsStates(exemptionStates);
        Transaction transactionWithNewCustomer = transaction.withCustomer(newCustomer);

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithNewCustomer)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithNewCustomer);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_CustomerHasFullyExemptionInState_ReturnsTrue() {
        // Given
        Map<String, ExemptionType> exemptionStates = new HashMap<String, ExemptionType>() {{
            put("CA", ExemptionType.FULLY);
        }};
        Customer newCustomer = customer.withExemptionsStates(exemptionStates);
        Transaction transactionWithNewCustomer = transaction.withCustomer(newCustomer);

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithNewCustomer)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithNewCustomer);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(true).verifyComplete();
    }

    @Test
    void isFullyExempted_NotExemptedBecauseDateExpired_ReturnsFalse() {
        // Given
        Map<String, ExemptionType> exemptionStates = new HashMap<String, ExemptionType>() {{
            put("CA", ExemptionType.FULLY);
        }};
        Customer newCustomer = customer.withExemptionsStates(exemptionStates);
        Transaction transactionWithNewCustomer = transaction.withCustomer(newCustomer);
        Transaction transactionWithDateLaterThanExemptionDate = transactionWithNewCustomer
                .withExternalTimeStamps(new TimeStamps(exemption.getValidationDates().getToDate().plusYears(1), LocalDateTime.now()));

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithDateLaterThanExemptionDate)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithDateLaterThanExemptionDate);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_NotExemptedBecauseDateIsYetToCome_ReturnsFalse() {
        // Given
        Map<String, ExemptionType> exemptionStates = new HashMap<String, ExemptionType>() {{
            put("CA", ExemptionType.FULLY);
        }};
        Customer newCustomer = customer.withExemptionsStates(exemptionStates);
        Transaction transactionWithNewCustomer = transaction.withCustomer(newCustomer);
        Transaction transactionWithDateLaterThanExemptionDate = transactionWithNewCustomer
                .withExternalTimeStamps(new TimeStamps(exemption.getValidationDates().getFromDate().minusYears(1), LocalDateTime.now()));

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithDateLaterThanExemptionDate)).thenReturn(Mono.just(exemption));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithDateLaterThanExemptionDate);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
    }

    @Test
    void isFullyExempted_ExemptionTypeIsPartially_ReturnsFalse() {
        // Given
        Map<String, ExemptionType> exemptionStates = new HashMap<>() {{
            put("CA", ExemptionType.FULLY);
        }};
        Exemption exemptionWithPartiallyType = exemption.withExemptionType(ExemptionType.PARTIALLY);

        Customer newCustomer = customer.withExemptionsStates(exemptionStates);
        Transaction transactionWithNewCustomer = transaction.withCustomer(newCustomer);
        Transaction transactionWithDateLaterThanExemptionDate = transactionWithNewCustomer
                .withExternalTimeStamps(new TimeStamps(exemption.getValidationDates().getFromDate().minusYears(1), LocalDateTime.now()));

        // When
        when(exemptionRepository.findByClientCustomerAndState(transactionWithDateLaterThanExemptionDate)).thenReturn(Mono.just(exemptionWithPartiallyType));
        Mono<Boolean> isFullyExemptedMono = exemptionService.isFullyExempted(transactionWithDateLaterThanExemptionDate);

        // Then
        StepVerifier.create(isFullyExemptedMono).expectNext(false).verifyComplete();
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
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.update(nullExemption,id));

        // Then
        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void update_NullIdPassed_ThrowsException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> exemptionService.update(exemption,nullId));

        // Then
        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

}
