package com.complyt.services;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.customer.ExemptionType;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.*;

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
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, clientId, null, externalTimeStamps);
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


}
