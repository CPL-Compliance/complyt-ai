package com.complyt.repository;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {
    @InjectMocks
    CustomerRepository customerRepository;

    @Mock
    MongoTemplate mongoTemplate;

    Customer customer;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        customer = new Customer(id, name, address);
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
    void findByName_NameWithLowerCaseExistsInTheCollection_ReturnsOneCustomer() {
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
    void findOneByName() {
    }

    @Test
    void getAllCustomers() {
    }

    @Test
    void save() {
    }
}