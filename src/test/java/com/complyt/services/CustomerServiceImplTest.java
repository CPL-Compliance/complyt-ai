package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
import com.complyt.repositories.CustomerRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerServiceImplTest {

    @InjectMocks
    CustomerServiceImpl customerServiceImpl;

    @Mock
    CustomerRepository customerRepository;

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
    void saveCustomer_CustomerSaved_CustomerReturned() {
        // Given

        // When
        when(customerRepository.save(customer)).thenReturn(Mono.just(customer));
        Mono<Customer> monoCustomer = customerServiceImpl.save(customer);

        // Then
        StepVerifier.create(monoCustomer).expectNext(customer).verifyComplete();
    }

    @Test
    void upsertCustomer_CustomerInserted_CustomerReturned(){
        // Given

        // When
        when(customerRepository.findByExternalId(customer.getExternalId())).thenReturn(Mono.just(customer));
        when(customerRepository.save(customer)).thenReturn(Mono.just(customer));
        Mono<Customer> customerMono = customerServiceImpl.upsert(customer);

        // Then
        StepVerifier.create(customerMono).expectNext(customer).verifyComplete();
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
        when(customerRepository.findByExternalId(id)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerServiceImpl.findByExternalId(id);

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
    void upsert_NullGiven_ThrowsNullPointerException() {
        // Given
        Customer nullCustomer = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerServiceImpl.upsert(nullCustomer);
        });

        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");
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
}