package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
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
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        Assertions.assertNotNull(fluxCustomers);
        assertEquals(customers.size(), 1);
        assertEquals(customers.get(0), customer);
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
        Assertions.assertNotNull(customers);
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
        Assertions.assertNotNull(customers);
        assertEquals(customers.size(), 1);
        assertEquals(customers.get(0), customer);
    }

    @Test
    void findByName_NameExists_ReturnsTwoCustomers() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));
        Customer customer2 = customer.withName("Existing Customer 2");
        when(reactiveMongoTemplate.find(query, Customer.class)).thenReturn(Flux.fromIterable(Arrays.asList(customer, customer2)));
        Flux<Customer> fluxCustomers = customerRepository.findByName(name);
        List<Customer> customers = fluxCustomers.collectList().block();

        // Then
        Assertions.assertNotNull(customers);
        assertEquals(customers.size(), 2);
        assertEquals(customers.get(0), customer);
        assertEquals(customers.get(1), customer2);
    }

    @Test
    void findOneByName_NameExists_ReturnsOneCustomer() {

    }

    @Test
    void getAllCustomers() {

    }

    @Test
    void save_NexCustomer_CustomerSaved() {
        // When
        String mongoId = UUID.randomUUID().toString();
        Customer dbCustomer = customer.withId(mongoId);
        when(reactiveMongoTemplate.save(customer)).thenReturn(Mono.just(dbCustomer));

        // Given
        Customer savedCustomer = customerRepository.save(customer);

        // Then
        Assertions.assertNotNull(savedCustomer);
        assertEquals(savedCustomer, dbCustomer);
    }

    @Test
    void upsert_NoExternalIdExists_InsertsNewCustomer() {
        // Given
        String externalId = "1000";

        // When
        Query query = Query.query(Criteria.where("externalId").is(externalId));
        Customer customer2 = customer.withExternalId(externalId);

        //UpdateResult updateresult = customerRepository.upsert(customer2);

        // Then
        //Assertions.assertNotNull(updateresult.getUpsertedId());

    }

    @Test
    void save_ExternalIdExists_UpdatesExistingCustomer() {
        // Given

        // When

        // Then

    }
}