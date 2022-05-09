package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
import com.complyt.services.CustomerService;
import com.complyt.v1.controllers.CustomerController;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerFacadeTest {

    @InjectMocks
    CustomerFacade customerFacade;

    @Mock
    CustomerService customerService;

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
    void initFacade_NullServiceInstanceGiven_ThrowsNullPointerException(){
        // Given
        CustomerService service = null;
        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            CustomerFacade facade = new CustomerFacade(service);
        });

        assertEquals(nullPointerException.getMessage(), "customerService is marked non-null but is null");
    }

    @Test
    void saveCustomer_CustomerSaved_CustomerReturned(){
        // Given

        // When
        when(customerService.save(customer)).thenReturn(Mono.just(customer));
        Mono<Customer> monoCustomer = customerFacade.save(customer);
        Customer returnedCustomer = monoCustomer.block();

        // Then
        assertNotNull(returnedCustomer);
        assertEquals(customer,returnedCustomer);
    }

    @Test
    void upsertCustomer_CustomerInserted_CustomerReturned() {
        // Given

        // When
        when(customerService.upsert(customer)).thenReturn(Mono.just(customer));
        Customer returnedCustomer = customerFacade.upsert(customer).block();

        // Then
        assertNotNull(returnedCustomer);
        assertEquals(customer,returnedCustomer);
    }

    @Test
    void getCustomerByName_CustomerFound_CustomerReturned() {
        // Given
        String name = "NameToSearchFor";

        // When
        when(customerService.findByName(name)).thenReturn(Flux.fromIterable(Arrays.asList(customer)));
        List<Customer> customers = customerFacade.findByName(name).collectList().block();

        // Then
        assertNotNull(customers);
        assertEquals(customers.size(),1);
    }

    @Test
    void getCustomerByExternalId_CustomerFound_CustomerReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer customerToSearchFor = customer.withExternalId(id);

        // When
        when(customerService.findByExternalId(id)).thenReturn(Mono.just(customerToSearchFor));
        Customer returnedCustomer = customerFacade.findByExternalId(id).block();

        // Then
        assertNotNull(returnedCustomer);
        assertEquals(returnedCustomer.getExternalId(),id);
        assertEquals(customerToSearchFor,returnedCustomer);
    }

    @Test
    void getAllCustomers_AllCustomersRetrieved_ReturnsAllCustomersFound() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withExternalId(id);
        List<Customer> allCustomers = new ArrayList<>();
        allCustomers.add(customer);
        allCustomers.add(secondCustomer);

        // When
        when(customerService.findAll()).thenReturn(Flux.fromIterable(allCustomers));
        List<Customer> returnedCustomers = customerFacade.getAllCustomers().collectList().block();

        // Then
        assertNotNull(returnedCustomers);
        assertEquals(returnedCustomers.size(),2);
    }
}