package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
import com.mongodb.client.result.UpdateResult;
import org.bson.BsonValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
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
    MongoTemplate mongoTemplate;

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

        when(mongoTemplate.find(query, Customer.class)).thenReturn(new ArrayList<Customer>() {{
            add(customer);
        }});
        List<Customer> customers = customerRepository.findByName(name);

        // Then
        Assertions.assertNotNull(customers);
        assertEquals(customers.size(), 1);
        assertEquals(customers.get(0), customer);
    }

    @Test
    void findByName_NameDoesntExist_ReturnsEmptyList() {
        // Given
        String name = "Existing Customer";

        // When
        Query query = Query.query(Criteria.where("name").regex("^" + name, "i"));

        when(mongoTemplate.find(query, Customer.class)).thenReturn(new ArrayList<>());
        List<Customer> customers = customerRepository.findByName(name);

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

        when(mongoTemplate.find(query, Customer.class)).thenReturn(new ArrayList<Customer>() {{
            add(customer);
        }});
        List<Customer> customers = customerRepository.findByName(name);

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
        when(mongoTemplate.find(query, Customer.class)).thenReturn(new ArrayList<Customer>() {{
            add(customer);
            add(customer2);
        }});
        List<Customer> customers = customerRepository.findByName(name);

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
    void save_NexCustomer_CustomerSaved(){
        // Given
        String mongoId = UUID.randomUUID().toString();
        Customer dbCustomer = customer.withId(mongoId);
        when(mongoTemplate.save(customer)).thenReturn(dbCustomer);

        // When
        Customer savedCustomer = customerRepository.save(customer);

        // Then
        Assertions.assertNotNull(savedCustomer);
        assertEquals(savedCustomer, dbCustomer);
    }

    @Test
    void upsert_ExternalIdDoesntExist_InsertsNewCustomer() {
        // Given
        String externalId = "1001";
        Customer dbCostumer = customer.withExternalId(externalId);

        // When
        Query query = Query.query(Criteria.where("externalId").is(dbCostumer.getExternalId()));
        Update update = new Update()
                .set("externalId", dbCostumer.getExternalId())
                .set("address", customer.getAddress())
                .set("name", customer.getName());

        UpdateResult expectedUpdateResult = UpdateResult.acknowledged(0, null, null);
        when(mongoTemplate.upsert(query,update,Customer.class)).thenReturn(expectedUpdateResult);
        when(customerRepository.findByExternalId(dbCostumer.getExternalId())).thenReturn(dbCostumer);
        customerRepository.upsert(dbCostumer);
        Customer dbCostumer2 = customerRepository.findByExternalId(externalId);
        // Then
        Assertions.assertNotNull(dbCostumer2);

    }

    @Test
    void upsert_ExternalIdExists_UpdateExistingCustomer() {
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