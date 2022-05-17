package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
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

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
//@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerRepositoryTest {
    @InjectMocks
    CustomerRepository customerRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Customer customer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        customer = new Customer(id, externalId, name, address);
    }

    @Test
    void findByName_NameExistsInTheCollection_ReturnsOneCustomer() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers)
                .expectNext(customer)
                .verifyComplete();
    }

    @Test
    void findById_IdDoesNotExist_ReturnsEmpty() {
        // Given


        // When
        when(reactiveMongoTemplate.findById(customer.getExternalId(), Customer.class)).thenReturn(Mono.empty());
        Mono<Customer> monoCustomer = customerRepository.findById(customer.getExternalId());

        // Then
        StepVerifier.create(monoCustomer).expectNextCount(0).verifyComplete();
    }


    @Test
    void findByName_NameDoesntExist_ReturnsEmpty() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.empty());
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers).expectNextCount(0).verifyComplete();
    }

    @Test
    void findByName_NameWithLowerCaseExists_ReturnsOneCustomer() {
        // Given
        String name = "existing customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers).expectNext(customer).verifyComplete();
    }

    @Test
    void findByName_NameExists_ReturnsTwoCustomers() {
        // Given
        String name = "Existing Customer";
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        Customer secondCustomer = customer.withName("SecondCustomer");

        // When
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.just(customer, secondCustomer));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);

        // Then
        StepVerifier.create(fluxCustomers).expectNext(customer, secondCustomer).verifyComplete();
    }

    @Test
    void findOneByName_NameExists_ReturnsOneCustomer() {
        // Given
        String nameToSearchFor = "NameToSearchFor";
        Customer customerToSearchFor = customer.withName(nameToSearchFor);
        Query query = Query.query(Criteria.where("name").is("^" + nameToSearchFor));

        // When
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerRepository.findOneByName(nameToSearchFor);

        // Then
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
    }

    @Test
    void getAllCustomers_RetrievingAllCustomersInDB_ExpectingTwoCustomers() {
        // Given
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id).withExternalId(externalId);

        //When
        when(reactiveMongoTemplate.findAll(Customer.class)).thenReturn(Flux.just(customer, secondCustomer));
        Flux<Customer> customerFlux = customerRepository.findAll();

        //Then
        StepVerifier.create(customerFlux).expectNext(customer, secondCustomer).verifyComplete();
    }

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
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerRepository.save(customer);
        });

        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");
    }

    @Test
    void findByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String name = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerRepository.findByName(name);
        });

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void findOneByName_NullGiven_ThrowsNullPointerException() {
        // Given
        String name = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerRepository.findOneByName(name);
        });

        assertEquals(nullPointerException.getMessage(), "name is marked non-null but is null");
    }

    @Test
    void findById_IdExists_ReturnsCustomerWithId() {
        // Given
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");

        // When
        when(reactiveMongoTemplate.findById(customerId, Customer.class)).thenReturn(Mono.just(customer));
        Mono<Customer> customerMono = customerRepository.findById(customerId);

        // Then
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
    }
}