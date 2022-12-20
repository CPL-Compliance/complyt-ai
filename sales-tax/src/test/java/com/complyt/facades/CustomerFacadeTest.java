package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.TimeStamps;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.services.CustomerService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        String tenantId = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        customer = new Customer(id, externalId, name, address, tenantId, CustomerType.RETAIL, null, null);
    }

    @Test
    void saveCustomer_CustomerSaved_CustomerReturned() {
        // Given
        Customer newCustomerWithUpdatedData = customer.withInternalTimeStamps(
                new TimeStamps(LocalDateTime.now(), LocalDateTime.now())
        );

        // When
        when(customerService.injectDataToNewCustomer(customer)).thenReturn(newCustomerWithUpdatedData);
        when(customerService.save(newCustomerWithUpdatedData)).thenReturn(Mono.just(newCustomerWithUpdatedData));
        Mono<Customer> customerMono = customerFacade.saveCustomer(customer);

        // Then
        StepVerifier.create(customerMono).expectNext(newCustomerWithUpdatedData).verifyComplete();
    }

    @Test
    void updateIfModified_CustomerModified_UpdatesCustomer() {
        // Given
        Customer newCustomer = customer.withName("newCustomer");
        Customer newCustomerWithUpdatedData = newCustomer.withInternalTimeStamps(
                new TimeStamps(LocalDateTime.now().minusDays(3), LocalDateTime.now())
        );

        // When
        when(customerService.injectDataToModifiedCustomer(newCustomer, customer)).thenReturn(newCustomerWithUpdatedData);
        when(customerService.update(newCustomerWithUpdatedData)).thenReturn(Mono.just(newCustomerWithUpdatedData));
        Mono<Customer> customerMono = customerFacade.updateIfModified(newCustomer, customer);

        // Then
        StepVerifier.create(customerMono).expectNext(newCustomerWithUpdatedData).verifyComplete();
    }


    @Test
    void getCustomerByName_CustomerFound_CustomerReturned() {
        // Given
        String name = "NameToSearchFor";

        // When
        when(customerService.findByName(name)).thenReturn(Flux.fromIterable(List.of(customer)));
        Flux<Customer> customers = customerFacade.findByName(name);

        // Then
        StepVerifier.create(customers).expectNextCount(1).verifyComplete();
    }

    @Test
    void getCustomerByExternalId_CustomerFound_CustomerReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer customerToSearchFor = customer.withExternalId(id);


        // When
        when(customerService.findByExternalId(id)).thenReturn(Mono.just(customerToSearchFor));
        Mono<Customer> customerMono = customerFacade.findByExternalId(id);

        // Then
        StepVerifier.create(customerMono).expectNext(customerToSearchFor).verifyComplete();
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

    @Test
    void saveCustomer_NullCustomerPassed_ThrowsException() {
        // Given
        Customer nullCustomer = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerFacade.saveCustomer(nullCustomer));

        // Then
        assertEquals(nullPointerException.getMessage(), "customer is marked non-null but is null");
    }

    @Test
    void updateIfModified_NullNewCustomerPassed_ThrowsException() {
        // Given
        Customer nullNewCustomer = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerFacade.updateIfModified(nullNewCustomer, customer));

        // Then
        assertEquals(nullPointerException.getMessage(), "newCustomer is marked non-null but is null");
    }

    @Test
    void updateIfModified_NullOriginalCustomerPassed_ThrowsException() {
        // Given
        Customer nullOriginalCustomer = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> customerFacade.updateIfModified(customer, nullOriginalCustomer));

        // Then
        assertEquals(nullPointerException.getMessage(), "originalCustomer is marked non-null but is null");
    }

}