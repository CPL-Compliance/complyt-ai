package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import com.complyt.services.CustomerService;
import org.bson.types.ObjectId;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

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
        ObjectId clientId = new ObjectId("507f191e810c19729de860ea");
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        customer = new Customer(id, externalId, name, address,clientId);
    }

    @Test
    void initFacade_NullServiceInstanceGiven_ThrowsNullPointerException() {
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
    void saveCustomer_CustomerSaved_CustomerReturned() throws InterruptedException {
        // Given
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(customerService.save(customer)).thenReturn(Mono.just(customer));
        customerFacade.save(customer).subscribe(returnedCustomer -> {
            atomicReference.set(returnedCustomer);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(atomicReference.get());
        assertEquals(customer, atomicReference.get());
    }

    @Test
    void upsertCustomer_CustomerInserted_CustomerReturned() throws InterruptedException {
        // Given
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(customerService.upsert(customer)).thenReturn(Mono.just(customer));
        customerFacade.upsert(customer).subscribe(returnedCustomer -> {
            atomicReference.set(returnedCustomer);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(atomicReference.get());
        assertEquals(customer, atomicReference.get());
    }

    @Test
    void getCustomerByName_CustomerFound_CustomerReturned() {
        // Given
        String name = "NameToSearchFor";

        // When
        when(customerService.findByName(name)).thenReturn(Flux.fromIterable(Arrays.asList(customer)));
        Flux<Customer> customers = customerFacade.findByName(name);

        // Then
        StepVerifier.create(customers).expectNextCount(1).verifyComplete();
    }

    @Test
    void getCustomerByExternalId_CustomerFound_CustomerReturned() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Customer customerToSearchFor = customer.withExternalId(id);
        AtomicReference<Customer> atomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(customerService.findByExternalId(id)).thenReturn(Mono.just(customerToSearchFor));
        customerFacade.findByExternalId(id).subscribe(returnedCustomer -> {
            atomicReference.set(returnedCustomer);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(atomicReference.get());
        assertEquals(atomicReference.get().getExternalId(), id);
        assertEquals(customerToSearchFor, atomicReference.get());
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
        Flux<Customer> returnedCustomers = customerFacade.getAllCustomers();

        // Then
        StepVerifier.create(returnedCustomers).expectNextCount(2).verifyComplete();
    }
}