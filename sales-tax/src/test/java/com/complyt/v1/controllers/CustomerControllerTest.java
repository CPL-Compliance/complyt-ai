package com.complyt.v1.controllers;

import com.complyt.domain.customer.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.customer.CustomerTypeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebFluxTest(CustomerController.class)
@WithMockUser(username = "mock", password = "mock")
class CustomerControllerTest {

    Customer customer;

    CustomerDto customerDto;

    CustomerController customerController;

    @MockBean
    private CustomerFacade customerFacade;

    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        customerDto = new CustomerDto(id, externalId, name, address, CustomerTypeDto.RETAIL, null, null);
        customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
    }

    @Test
    void upsert_NewCustomerCreated_SavesCustomer() {
        // Given
        String externalId = customerDto.getExternalId();
        Customer mappedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.empty());
        when(customerFacade.saveCustomer(mappedCustomer)).thenReturn(Mono.just(mappedCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerDto.class)
                .value(customerItem -> customerItem, equalTo(customerDto));
    }

    @Test
    void upsert_CustomerExists_UpdatesCustomer() {
        // Given
        String externalId = customer.getExternalId();
        Customer newCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Customer originalCustomer = newCustomer.withName("originalCustomer");
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.just(originalCustomer));
        when(customerFacade.saveCustomer(newCustomer)).thenReturn(Mono.empty());
        when(customerFacade.updateIfModified(newCustomer, originalCustomer)).thenReturn(Mono.just(newCustomer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/" + externalId)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerItem -> customerItem, equalTo(customerDto));
    }

    @Test
    void update_UpdateFails_Returns5xxServerError() {
        // Given
        when(customerFacade.upsert(customer)).thenThrow(OperationFailedException.class);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/" + customer.getExternalId())
                        .build())
                .bodyValue(customer)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getByExternalId_FindsCustomer_ReturnsCustomer() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerItem -> customerItem, equalTo(customerDto));
    }

    @Test
    void getByExternalId_OperationFails_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getByName_FindsCustomer_ReturnsCustomer() {
        // Given
        String name = "name";
        List<Customer> customersFoundByName = new ArrayList<>() {{
            add(customer);
        }};
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(customersFoundByName));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/name/" + name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk().
                expectBodyList(Customer.class)
                .value(customers -> customers, equalTo(customersFoundByName));
    }

    @Test
    void getAll_AllCustomersRetrieved_ReturnsAllCustomersFound() {
        // Given
        String id = UUID.randomUUID().toString();
        Customer secondCustomer = customer.withId(id);

        List<Customer> allCustomers = new ArrayList<>() {{
            add(customer);
            add(secondCustomer);
        }};

        when(customerFacade.getAllCustomers()).thenReturn(Flux.fromIterable(allCustomers));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .value(customers -> customers, equalTo(allCustomers));
    }

    @Test
    void upsertCustomer_NullExternalId_ThrowsNullPointerException() {
        // Given
        String nullExternalId = null;
        customerController = new CustomerController(customerFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerController.upsert(nullExternalId, customerDto);
        });

        // Then
        assertEquals("externalId is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void upsertCustomer_NullCustomerDto_ThrowsNullPointerException() {
        //Given
        String externalId = UUID.randomUUID().toString();
        CustomerDto nullCustomerDto = null;
        customerController = new CustomerController(customerFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerController.upsert(externalId, nullCustomerDto);
        });

        // Then
        assertEquals("customerDto is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void getByExternalId_NullExternalId_ThrowsNullPointerException() {
        //Given
        String nullExternalId = null;
        customerController = new CustomerController(customerFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerController.getByExternalId(nullExternalId);
        });

        // Then
        assertEquals("externalId is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void getByName_NullName_ThrowsNullPointerException() {
        //Given
        String nullName = null;
        customerController = new CustomerController(customerFacade);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerController.getByName(nullName);
        });

        // Then
        assertEquals("name is marked non-null but is null", nullPointerException.getMessage());
    }
}