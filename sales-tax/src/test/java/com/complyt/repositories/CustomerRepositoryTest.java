package com.complyt.repositories;

import com.complyt.config.SecurityConfigMockTest;
import com.complyt.domain.Address;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.security.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfigMockTest.class)
class CustomerRepositoryTest {
    @InjectMocks
    CustomerRepository customerRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Customer customer;
    User user;

    @BeforeEach
    void setUp() {
        ObjectId clientId = new ObjectId("507f191e810c19729de860ea");
        user = User.builder().username("user").password("password").clientId(clientId).build();
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        customer = new Customer(id, externalId, name, address, clientId, CustomerType.RETAIL);
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByName_NameExistsInTheCollection_ReturnsOneCustomer() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("clientId").is(user.getClientId()));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers)
                .expectNext(customer)
                .verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findById_IdDoesNotExist_ReturnsEmpty() {
        // Given
        String id = UUID.randomUUID().toString();

        // When
        Query query = Query.query(Criteria.where("_id").is(id)
                .and("clientId").is(user.getClientId()));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer.withId(id)));
        Mono<Customer> monoCustomer = customerRepository.findById(id);

        // Then
        StepVerifier.create(monoCustomer).expectNext(customer.withId(id)).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByName_NameDoesntExist_ReturnsEmpty() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("clientId").is(user.getClientId()));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.empty());
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers).expectNextCount(0).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByName_NameWithLowerCaseExists_ReturnsOneCustomer() {
        // Given
        String name = "existing customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("clientId").is(user.getClientId()));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers).expectNext(customer).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByName_NameExists_ReturnsTwoCustomers() {
        // Given
        String name = "Existing Customer";
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i")
                .and("clientId").is(user.getClientId()));
        Customer secondCustomer = customer.withName("SecondCustomer");

        // When
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer, secondCustomer));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers).expectNext(customer, secondCustomer).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findOneByName_NameExists_ReturnsOneCustomer() {
        // Given
        String name = "NameToSearchFor";
        Customer customerToSearchFor = customer.withName(name);
        Query query = Query.query(Criteria.where("name").is("^" + name)
                .and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerRepository.findOneByName(name);

        // Then
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void getAllCustomers_RetrievingAllCustomersInDB_ExpectingTwoCustomers() {
        // Given
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id).withExternalId(externalId);
        Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));

        //When
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer, secondCustomer));
        Flux<Customer> customerFlux = customerRepository.findAll();

        //Then
        StepVerifier.create(customerFlux).expectNext(customer, secondCustomer).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void save_NexCustomer_CustomerSaved() {
        // Given
        String mongoId = UUID.randomUUID().toString();
        Customer dbCustomer = customer.withId(mongoId);
        when(reactiveMongoTemplate.save(customer)).thenReturn(Mono.just(dbCustomer));

        // When
        Mono<Customer> monoSavedCustomer = customerRepository.save(customer);

        // Then
        StepVerifier.create(monoSavedCustomer).expectNext(dbCustomer).verifyComplete();
    }

    @Test
    void saveCustomer_Null_ThrowsNullPointerException() {
        // Given
        Customer customer = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerRepository.save(customer));

        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");
    }

    @Test
    void findByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String name = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerRepository.findByName(name));

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void findOneByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String name = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerRepository.findOneByName(name));

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findById_IdExists_ReturnsCustomerWithId() {
        // Given
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Query query = Query.query(Criteria.where("_id").is(customerId)
                .and("clientId").is(user.getClientId()));
        // When
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer));
        Mono<Customer> customerMono = customerRepository.findById(customerId);

        // Then
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByExternalId_IdExists_ReturnsCustomer() {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(customer.getExternalId())
                .and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customer));

        Mono<Customer> customerMono = customerRepository.findByExternalId(customer.getExternalId());

        // Then
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByExternalId_IdNotExists_ReturnsEmpty() {
        // Given
        String id = UUID.randomUUID().toString();
        Query query = Query.query(Criteria.where("externalId").is(id)
                .and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.empty());

        Mono<Customer> customerMono = customerRepository.findByExternalId(id);

        // Then
        StepVerifier.create(customerMono).expectNextCount(0).verifyComplete();
    }
}