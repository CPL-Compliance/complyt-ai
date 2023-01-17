package com.complyt.services;

import com.complyt.business.complyt_id.ComplytIdHandler;
import com.complyt.business.complyt_id.CustomerComplytIdHandler;
import com.complyt.business.timestamps_injection.ExistingCustomerInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewCustomerInternalTimestampsInjector;
import com.complyt.domain.Address;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.repositories.CustomerRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceImplTest {

    @InjectMocks
    CustomerServiceImpl customerServiceImpl;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CustomerComplytIdHandler customerComplytIdHandler;

    Customer customer;

    String source;

    DomainObjectStub domainObjectStub;

    @BeforeAll
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        String name = "Existing Customer";
        customer = domainObjectStub.createCustomer(UUID.randomUUID().toString()).withName(name);
        source = domainObjectStub.getUnifiedSource();
    }

    @Test
    void injectDataToExistingCustomer_InjectsDataToExistingCustomer_ReturnsCustomer() {
        // Given
        Customer newCustomer = customer.withName("NewName");
        ExistingCustomerInternalTimestampsInjector injector = new ExistingCustomerInternalTimestampsInjector(customer);
        Customer customerWithUpdatedDates = injector.inject();

        Customer actualCustomer = customerServiceImpl.injectDataToExistingCustomer(newCustomer, customer).block();

        // Then
        LocalDateTime expectedCreatedDateTime = customerWithUpdatedDates.getInternalTimestamps().getCreatedDate().getTimestamp();
        LocalDateTime expectedUpdatedDateTime = customerWithUpdatedDates.getInternalTimestamps().getUpdatedDate().getTimestamp();

        LocalDateTime actualCreatedDateTime = actualCustomer.getInternalTimestamps().getCreatedDate().getTimestamp();
        LocalDateTime actualUpdatedDateTime = actualCustomer.getInternalTimestamps().getUpdatedDate().getTimestamp();

        Assertions.assertEquals(expectedUpdatedDateTime.getYear(), actualUpdatedDateTime.getYear());
        Assertions.assertEquals(expectedUpdatedDateTime.getMonthValue(), actualUpdatedDateTime.getMonthValue());
        Assertions.assertEquals(expectedUpdatedDateTime.getDayOfYear(), actualUpdatedDateTime.getDayOfYear());
        Assertions.assertEquals(expectedUpdatedDateTime.getHour(), actualUpdatedDateTime.getHour());
        Assertions.assertEquals(expectedCreatedDateTime.getYear(), actualCreatedDateTime.getYear());
        Assertions.assertEquals(expectedCreatedDateTime.getMonthValue(), actualCreatedDateTime.getMonthValue());
        Assertions.assertEquals(expectedCreatedDateTime.getDayOfYear(), actualCreatedDateTime.getDayOfYear());
        Assertions.assertEquals(expectedCreatedDateTime.getHour(), actualCreatedDateTime.getHour());
    }

    @Test
    void injectDataToNewCustomer_InjectsDataToNewCustomer_ReturnsCustomer() {
        // Given
        UUID complytId = UUID.randomUUID();
        NewCustomerInternalTimestampsInjector injector = new NewCustomerInternalTimestampsInjector(customer.withComplytId(null));
        Customer customerWithUpdatedDates = injector.inject().withComplytId(complytId);

        // when
        Mono<Customer> actualCustomerMono = customerServiceImpl.injectDataToNewCustomer(customer);

        // Then
        StepVerifier.create(actualCustomerMono).assertNext(actualCustomer -> {
            LocalDateTime expectedCreatedDateTime = customerWithUpdatedDates.getInternalTimestamps().getCreatedDate().getTimestamp();
            LocalDateTime expectedUpdatedDateTime = customerWithUpdatedDates.getInternalTimestamps().getUpdatedDate().getTimestamp();

            LocalDateTime actualCreatedDateTime = actualCustomer.getInternalTimestamps().getCreatedDate().getTimestamp();
            LocalDateTime actualUpdatedDateTime = actualCustomer.getInternalTimestamps().getUpdatedDate().getTimestamp();

            Assertions.assertEquals(expectedUpdatedDateTime.getYear(), actualUpdatedDateTime.getYear());
            Assertions.assertEquals(expectedUpdatedDateTime.getMonthValue(), actualUpdatedDateTime.getMonthValue());
            Assertions.assertEquals(expectedUpdatedDateTime.getDayOfYear(), actualUpdatedDateTime.getDayOfYear());
            Assertions.assertEquals(expectedUpdatedDateTime.getHour(), actualUpdatedDateTime.getHour());
            Assertions.assertEquals(expectedCreatedDateTime.getYear(), actualCreatedDateTime.getYear());
            Assertions.assertEquals(expectedCreatedDateTime.getMonthValue(), actualCreatedDateTime.getMonthValue());
            Assertions.assertEquals(expectedCreatedDateTime.getDayOfYear(), actualCreatedDateTime.getDayOfYear());
            Assertions.assertEquals(expectedCreatedDateTime.getHour(), actualCreatedDateTime.getHour());
            assertEquals(customerWithUpdatedDates.getComplytId(), actualCustomer.getComplytId());
        });
        /*LocalDateTime expectedCreatedDateTime = customerWithUpdatedDates.getInternalTimestamps().getCreatedDate().getTimestamp();
        LocalDateTime expectedUpdatedDateTime = customerWithUpdatedDates.getInternalTimestamps().getUpdatedDate().getTimestamp();

        LocalDateTime actualCreatedDateTime = actualCustomer.getInternalTimestamps().getCreatedDate().getTimestamp();
        LocalDateTime actualUpdatedDateTime = actualCustomer.getInternalTimestamps().getUpdatedDate().getTimestamp();

        Assertions.assertEquals(expectedUpdatedDateTime.getYear(), actualUpdatedDateTime.getYear());
        Assertions.assertEquals(expectedUpdatedDateTime.getMonthValue(), actualUpdatedDateTime.getMonthValue());
        Assertions.assertEquals(expectedUpdatedDateTime.getDayOfYear(), actualUpdatedDateTime.getDayOfYear());
        Assertions.assertEquals(expectedUpdatedDateTime.getHour(), actualUpdatedDateTime.getHour());
        Assertions.assertEquals(expectedCreatedDateTime.getYear(), actualCreatedDateTime.getYear());
        Assertions.assertEquals(expectedCreatedDateTime.getMonthValue(), actualCreatedDateTime.getMonthValue());
        Assertions.assertEquals(expectedCreatedDateTime.getDayOfYear(), actualCreatedDateTime.getDayOfYear());
        Assertions.assertEquals(expectedCreatedDateTime.getHour(), actualCreatedDateTime.getHour());*/
    }

    @Test
    void save_CustomerSaved_CustomerReturned() {
        // Given
        Customer customerWithId = customer.withId(UUID.randomUUID().toString());

        // When
        when(customerRepository.save(any())).thenReturn(Mono.just(customerWithId));
        Mono<Customer> monoCustomer = customerServiceImpl.save(customer);

        // Then
        StepVerifier.create(monoCustomer).expectNextMatches(returnedCustomer -> {
                    Timestamps internalTimeStamps = returnedCustomer.getInternalTimestamps();
                    Customer customerWithIdAndTimeStamps = customerWithId.withInternalTimestamps(internalTimeStamps);
                    return customerWithIdAndTimeStamps == customerWithId;
                })
                .expectComplete().verify();

    }

    @Test
    void update_CustomerInserted_CustomerReturned() {
        // Given

        // When
        when(customerRepository.findByExternalId(customer.getExternalId(), source)).thenReturn(Mono.just(customer));
        when(customerRepository.save(any())).thenReturn(Mono.just(customer));
        Mono<Customer> customerMono = customerServiceImpl.update(customer);

        // Then
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
    }

    @Test
    void update_CustomerDoesNotExist_ThrowsNotFoundError() {
        // Given

        // When
        when(customerRepository.findByExternalId(customer.getExternalId(), source)).thenReturn(Mono.empty());
        Mono<Customer> customerMono = customerServiceImpl.update(customer);

        // Then
        StepVerifier.create(customerMono).expectError().verify();
    }

    @Test
    void findOneByName_FindsCustomer_ReturnsCustomer() {
        // Given
        String name = "CustomerToSearchFor";
        Customer customerToSearchFor = customer.withName(name);

        // When
        when(customerRepository.findOneByName(name)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerServiceImpl.findOneByName(name);

        // Then
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
    }

    @Test
    void findByExternalId_CustomerFound_ReturnsCustomer() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer customerToSearchFor = customer.withExternalId(id);

        // When
        when(customerRepository.findByExternalId(id,source)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerServiceImpl.findByExternalId(id,source);

        // Then
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
    }

    @Test
    void findByName_TwoCustomersFound_ReturnsTwoCustomers() {
        // Given
        Customer secondCustomer = customer.withName(customer.getName());

        // When
        when(customerRepository.findByName(customer.getName())).thenReturn(Flux.just(customer, secondCustomer));
        Flux<Customer> customerFlux = customerServiceImpl.findByName(customer.getName());

        // Then
        StepVerifier.create(customerFlux).expectNext(customer, secondCustomer).verifyComplete();
    }

    @Test
    void findById_CustomerFound_ReturnsCustomer() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer customerToSearchFor = customer.withId(id);

        // When
        when(customerRepository.findById(id)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerServiceImpl.findById(id);

        // Then
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
    }

    @Test
    void findByObjectId_CustomerFound_ReturnsCustomer() {
        // Given
        ObjectId id = new ObjectId();
        Customer customerToSearchFor = customer.withId(id.toString());

        // When
        when(customerRepository.findById(id)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerServiceImpl.findById(id);

        // Then
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
    }

    @Test
    void getAllCustomers_AllCustomersReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withExternalId(id);

        // When
        when(customerRepository.findAll()).thenReturn(Flux.just(customer, secondCustomer));
        Flux<Customer> customerFlux = customerServiceImpl.findAll();

        // Then
        StepVerifier.create(customerFlux).expectNext(customer, secondCustomer).verifyComplete();
    }

    @Test
    void findAllBySource_SourceExists_Returns2Customers() {
        // Given
        Customer secondsCustomer = domainObjectStub.createCustomer(new ObjectId().toString());

        // When
        when(customerRepository.findAllBySource(source)).thenReturn(Flux.just(customer,secondsCustomer));
        Flux<Customer> customerFlux = customerServiceImpl.findAllBySource(source);

        // Then
        StepVerifier.create(customerFlux).expectNext(customer, secondsCustomer).verifyComplete();
    }

    @Test
    void findByComplytId_complytIdExists_ReturnsCustomer() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        when(customerRepository.findByComplytId(complytId)).thenReturn(Mono.just(customer.withComplytId(complytId)));
        Mono<Customer> customerMono = customerServiceImpl.findByComplytId(complytId);

        // Then
        StepVerifier.create(customerMono).expectNext(customer.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void checkCustomerNotHavingComplytId_DoesntHaveComplytId_ReturnsCustomer() {
        // Given When
        when(customerComplytIdHandler.isNewDontHaveComplytId(customer)).thenReturn(Mono.just(customer));
        Mono<Customer> customerMono = customerServiceImpl.checkCustomerNotHavingComplytId(customer);

        // Then
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
    }

    @Test
    void checkCustomerNotHavingComplytId_DoesHaveComplytId_ThrowsException() {
        // Given When
        when(customerComplytIdHandler.isNewDontHaveComplytId(customer)).thenReturn(Mono.empty());
        Mono<Customer> customerMono = customerServiceImpl.checkCustomerNotHavingComplytId(customer);

        // Then
        StepVerifier.create(customerMono).expectErrorMessage("cannot insert new transaction with complyt id").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdMotEquals_ThrowsExceptions() {
        // Given
        Customer newCustomer = customer.withComplytId(UUID.randomUUID());

        // When
        when(customerComplytIdHandler.isComplytIdOfUpdatedEqualsToOld( newCustomer, customer)).thenReturn(Mono.empty());
        Mono<Customer> customerMono = customerServiceImpl.checkComplytIdOfModifiedEqualsToOriginal(newCustomer, customer);

        // Then
        StepVerifier.create(customerMono).expectErrorMessage("modified and original customer's complytIds not equal").verify();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_DoesNotHaveComplytId_ReturnsNewCustomer() {
        // Given
        Customer newCustomer = customer.withComplytId(null);

        // When
        when(customerComplytIdHandler.isComplytIdOfUpdatedEqualsToOld( newCustomer, customer)).thenReturn(Mono.just(newCustomer));
        Mono<Customer> customerMono = customerServiceImpl.checkComplytIdOfModifiedEqualsToOriginal(newCustomer, customer);

        // Then
        StepVerifier.create(customerMono).expectNext(newCustomer).verifyComplete();
    }

    @Test
    void checkComplytIdOfModifiedEqualsToOriginal_ComplytIdAreEquals_ReturnsNewCustomer() {
        // Given
        Customer newCustomer = customer.withComplytId(customer.getComplytId());

        // When
        when(customerComplytIdHandler.isComplytIdOfUpdatedEqualsToOld( newCustomer, customer)).thenReturn(Mono.just(newCustomer));
        Mono<Customer> customerMono = customerServiceImpl.checkComplytIdOfModifiedEqualsToOriginal(newCustomer, customer);

        // Then
        StepVerifier.create(customerMono).expectNext(newCustomer).verifyComplete();
    }

    @Test
    void save_NullGiven_ThrowsNullPointerException() {
        // Given
        Customer nullCustomer = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerServiceImpl.save(nullCustomer);
        });

        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");
    }

    @Test
    void upsert_NullCustomerGiven_ThrowsNullPointerException() {
        // Given
        Customer nullCustomer = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerServiceImpl.update(nullCustomer);
        });

        assertEquals(nullPointerException.getMessage(), "newCustomer is marked non-null but is null");
    }



    @Test
    void findOneByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String nullName = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerServiceImpl.findOneByName(nullName);
        });

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void findByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String nullName = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerServiceImpl.findByName(nullName);
        });

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void findById_NullGiven_ThrowsNullPointerException() {
        // Given
        String nullId = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerServiceImpl.findById(nullId);
        });

        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }

    @Test
    void findByObjectId_NullGiven_ThrowsNullPointerException() {
        // Given
        ObjectId nullId = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerServiceImpl.findById(nullId);
        });

        assertEquals(nullPointerException.getMessage(), "id is marked non-null but is null");
    }
}