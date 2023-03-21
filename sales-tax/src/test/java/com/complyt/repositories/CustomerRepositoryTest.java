package com.complyt.repositories;

import com.complyt.domain.customer.Customer;
import com.complyt.security.TenantResolver;
import org.bson.types.ObjectId;
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
import testUtils.ut.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {
    @InjectMocks
    CustomerRepository customerRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    @Mock
    TenantResolver tenantResolver;

    Customer customer;
    String tenantId;

    String source;

    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        tenantId = UUID.randomUUID().toString();
        testUtilities = new TestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        customer = testUtilities.createCustomer(id).withExternalId(externalId).withName("Existing Customer");
        source = testUtilities.getUnifiedSource();
    }

    @Test
    void findByName_NameExistsInTheCollection_ReturnsOneCustomer() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("tenantId").is(tenantId));

        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer));

        // Then
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        StepVerifier.create(fluxCustomers)
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    void findById_IdDoesNotExist_ReturnsEmpty() {
        // Given
        String id = UUID.randomUUID().toString();

        // When
        Query query = Query.query(Criteria.where("_id").is(id).and("tenantId").is(tenantId));
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer.withId(id)));

        // Then
        Mono<Customer> monoCustomer = customerRepository.findById(id);
        StepVerifier.create(monoCustomer).expectNext(customer.withId(id)).verifyComplete();
    }

    @Test
    void findByComplytId_IdDoesNotExist_ReturnsEmpty() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(tenantId));
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.empty());

        // Then
        Mono<Customer> monoCustomer = customerRepository.findByComplytId(complytId);
        StepVerifier.create(monoCustomer).verifyComplete();
    }

    @Test
    void findByComplytId_IdExist_ReturnsCustomer() {
        // Given
        UUID complytId = UUID.randomUUID();

        // When
        Query query = Query.query(Criteria.where("complytId").is(complytId)
                .and("tenantId").is(tenantId));
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer.withComplytId(complytId)));

        // Then
        Mono<Customer> monoCustomer = customerRepository.findByComplytId(complytId);
        StepVerifier.create(monoCustomer).expectNext(customer.withComplytId(complytId)).verifyComplete();
    }

    @Test
    void findByName_NameDoesntExist_ReturnsEmpty() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("tenantId").is(tenantId));

        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.empty());

        // Then
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        StepVerifier.create(fluxCustomers).expectNextCount(0).verifyComplete();
    }

    @Test
    void findByName_NameWithLowerCaseExists_ReturnsOneCustomer() {
        // Given
        String name = "existing customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("tenantId").is(tenantId));

        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer));

        // Then
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        StepVerifier.create(fluxCustomers).expectNext(customer).verifyComplete();
    }

    @Test
    void findByName_NameExists_ReturnsTwoCustomers() {
        // Given
        String name = "Existing Customer";
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("tenantId").is(tenantId));

        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        Customer secondCustomer = customer.withName("SecondCustomer");

        // When
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer, secondCustomer));

        // Then
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        StepVerifier.create(fluxCustomers).expectNext(customer, secondCustomer).verifyComplete();
    }

    @Test
    void findOneByName_NameExists_ReturnsOneCustomer() {
        // Given
        String name = "NameToSearchFor";
        Customer customerToSearchFor = customer.withName(name);
        Query query = Query.query(Criteria.where("name").is("^" + name)
                .and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customerToSearchFor));

        // Then
        Mono<Customer> customerMono = customerRepository.findOneByName(name);
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
    }

    @Test
    void getAllCustomers_RetrievingAllCustomersInDB_ExpectingTwoCustomers() {
        // Given
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id).withExternalId(externalId);
        Query query = Query.query(Criteria.where("tenantId").is(tenantId));

        //When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer, secondCustomer));

        //Then
        Flux<Customer> customerFlux = customerRepository.findAll();
        StepVerifier.create(customerFlux).expectNext(customer, secondCustomer).verifyComplete();
    }

    @Test
    void getAllCustomersBySource_RetrievingAllCustomersInSource_ExpectingTwoCustomers() {
        // Given
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id).withExternalId(externalId);
        Query query = Query.query(Criteria.where("tenantId").is(tenantId)
                .and("source").is(source));

        //When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer, secondCustomer));

        //Then
        Flux<Customer> customerFlux = customerRepository.findAllBySource(source);
        StepVerifier.create(customerFlux).expectNext(customer, secondCustomer).verifyComplete();
    }

    @Test
    void save_NexCustomer_CustomerSaved() {
        // Given
        String mongoId = UUID.randomUUID().toString();
        Customer dbCustomer = customer.withId(mongoId);
        when(reactiveMongoTemplate.save(customer)).thenReturn(Mono.just(dbCustomer));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(customer.getTenantId()));

        // Then
        Mono<Customer> monoSavedCustomer = customerRepository.save(customer);
        StepVerifier.create(monoSavedCustomer).expectNext(dbCustomer).verifyComplete();
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void saveCustomer_Null_ThrowsNullPointerException() {
        // Given
        Customer customer = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerRepository.save(customer));

        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String name = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerRepository.findByName(name));

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    void findOneByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String name = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerRepository.findOneByName(name));

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void findById_IdExists_ReturnsCustomerWithId() {
        // Given
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Query query = Query.query(Criteria.where("_id").is(customerId)
                .and("tenantId").is(tenantId));
        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer));

        // Then
        Mono<Customer> customerMono = customerRepository.findById(customerId);
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
    }

    @Test
    void findByExternalIdAndSource_IdExists_ReturnsCustomer() {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(customer.getExternalId())
                .and("source").is(source)
                .and("tenantId").is(tenantId));

        // When
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer));

        // Then
        Mono<Customer> customerMono = customerRepository.findByExternalIdAndSource(customer.getExternalId(), source);
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
    }

    @Test
    void findByExternalIdAndSource_IdNotExists_ReturnsEmpty() {
        // Given
        String id = UUID.randomUUID().toString();
        Query query = Query.query(Criteria.where("externalId").is(id)
                .and("source").is(source)
                .and("tenantId").is(tenantId));

        // When
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.empty());
        when(tenantResolver.resolve()).thenReturn(Mono.just(tenantId));

        // Then
        Mono<Customer> customerMono = customerRepository.findByExternalIdAndSource(id, source);

        StepVerifier.create(customerMono).expectNextCount(0).verifyComplete();
    }
}