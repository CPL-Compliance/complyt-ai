package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.mongodb.client.result.UpdateResult;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerRepositoryTest {
    @InjectMocks
    CustomerRepository customerRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;
 
    Customer customer;

    @BeforeAll
    void setUp() {
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

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.fromIterable(Arrays.asList(customer)));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        List<Customer> customers = fluxCustomers.collectList().block();

        // Then
        assertNotNull(fluxCustomers);
        assertEquals(customers.size(), 1);
        assertEquals(customers.get(0), customer);
    }

    @Test
    void findOneById_IdDoesNotExist_ReturnsNull(){
        // Given


        // When
        when(reactiveMongoTemplate.findById(customer.getExternalId(), Customer.class)).thenReturn(null);
        Mono<Customer> monoCustomer = customerRepository.findById(customer.getExternalId());

        // Then
        Assert.assertEquals(monoCustomer,null);
    }


    @Test
    void findByName_NameDoesntExist_ReturnsEmptyList() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.fromIterable(Arrays.asList()));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        List<Customer> customers = fluxCustomers.collectList().block();

        // Then
        assertNotNull(customers);
        assertEquals(customers.size(), 0);
    }

    @Test
    void findByName_NameWithLowerCaseExists_ReturnsOneCustomer() {
        // Given
        String name = "existing customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.fromIterable(Arrays.asList(customer)));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        List<Customer> customers = fluxCustomers.collectList().block();

        // Then
        assertNotNull(customers);
        assertEquals(customers.size(), 1);
        assertEquals(customers.get(0), customer);
    }

    @Test
    void findByName_NameExists_ReturnsTwoCustomers() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        Customer secondCustomer = customer.withName("SecondCustomer");
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.fromIterable(Arrays.asList(customer, secondCustomer)));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        List<Customer> customers = fluxCustomers.collectList().block();

        // Then
        assertNotNull(customers);
        assertEquals(customers.size(), 2);
        assertEquals(customers.get(0), customer);
        assertEquals(customers.get(1), secondCustomer);
    }

    @Test
    void findOneByName_NameExists_ReturnsOneCustomer() {
        // Given
        String nameToSearchFor = "NameToSearchFor";
        Customer customerToSearchFor = customer.withName(nameToSearchFor);
        Query query = Query.query(Criteria.where("name").is("^" + nameToSearchFor));

        // When
        when(reactiveMongoTemplate.findOne(query,Customer.class)).thenReturn(Mono.just(customerToSearchFor));
        Customer returnedCustomer = customerRepository.findOneByName(nameToSearchFor).block();

        // Then
        assertNotNull(returnedCustomer);
        assertEquals(returnedCustomer, customerToSearchFor);
    }

    @Test
    void getAllCustomers_RetrievingAllCustomersInDB_ExpectingTwoCustomers() {
        // Given
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id).withExternalId(externalId);
        List<Customer> customers = new ArrayList<Customer>() {{
            add(customer);
            add(secondCustomer);
        }};

        //When
        when(reactiveMongoTemplate.findAll(Customer.class)).thenReturn(Flux.fromIterable(customers));
        List<Customer> returnedCustomers = customerRepository.findAll().collectList().block();

        //Then
        assertNotNull(returnedCustomers);
        assertEquals(returnedCustomers,customers);
    }

    @Test
    void save_NexCustomer_CustomerSaved() {
        // Given
        String mongoId = UUID.randomUUID().toString();
        Customer dbCustomer = customer.withId(mongoId);
        when(reactiveMongoTemplate.save(customer)).thenReturn(Mono.just(dbCustomer));

        // When
        Customer savedCustomer = customerRepository.save(customer);

        // Then
        assertNotNull(savedCustomer);
        assertEquals(savedCustomer, dbCustomer);
    }

    @Test
    void upsert_ExternalIdDoesntExist_InsertsNewCustomer() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Customer customerWithNewExternalId = customer.withExternalId(externalId);

        Query query = Query.query(Criteria.where("externalId").is(customerWithNewExternalId.getExternalId()));

        Update update = new Update()
                .set("externalId", customerWithNewExternalId.getExternalId())
                .set("address", customer.getAddress())
                .set("name", customer.getName());
        UpdateResult expectedUpdateResult = UpdateResult.acknowledged(0, null, null);

        // When
        when(reactiveMongoTemplate.upsert(query,update,Customer.class)).thenReturn(Mono.just(expectedUpdateResult));
        when(reactiveMongoTemplate.findOne(query,Customer.class)).thenReturn(Mono.just(customerWithNewExternalId));
        Customer insertedCustomer = customerRepository.upsert(customerWithNewExternalId).block();

        // Then
        assertNotNull(insertedCustomer);
        assertEquals(customerWithNewExternalId, insertedCustomer);
    }

    @Test
    void upsert_UpsertNotAcknowledgedExist_ThrowsAnError() {
        // Given

        Query query = Query.query(Criteria.where("externalId").is(customer.getExternalId()));
        Update update = new Update()
                .set("externalId", customer.getExternalId())
                .set("address", customer.getAddress())
                .set("name", customer.getName());
        UpdateResult expectedUpdateResult = UpdateResult.unacknowledged();

        // When
        when(reactiveMongoTemplate.upsert(query,update,Customer.class)).thenReturn(Mono.just(expectedUpdateResult));

        Exception exception = assertThrows(OperationFailedException.class, () -> {
            customerRepository.upsert(customer).block();
        });

        String expectedMessage = "Could not update customer";
        String actualMessage = exception.getMessage();

        // Then
        assertTrue(actualMessage.contains(expectedMessage));
    }


    @Test
    void upsert_ExternalIdExists_UpdateExistingCustomer() {
        // Given
        String newName = "newName";
        Customer existingCustomerWithNewName = customer.withName(newName);
        Query query = Query.query(Criteria.where("externalId").is(existingCustomerWithNewName.getExternalId()));

        Update update = new Update()
                .set("externalId", existingCustomerWithNewName.getExternalId())
                .set("address", existingCustomerWithNewName.getAddress())
                .set("name", existingCustomerWithNewName.getName());
        UpdateResult expectedResult = UpdateResult.acknowledged(1,null,null);

        // When
        when(reactiveMongoTemplate.upsert(query,update,Customer.class)).thenReturn(Mono.just(expectedResult));
        when(reactiveMongoTemplate.findOne(query, Customer.class)).thenReturn(Mono.just(existingCustomerWithNewName));
        Customer updatedCustomer = customerRepository.upsert(existingCustomerWithNewName).block();

        // Then
        assertNotNull(updatedCustomer);
        Assertions.assertEquals(existingCustomerWithNewName,updatedCustomer);
        Assertions.assertEquals(newName,updatedCustomer.getName());
    }

}