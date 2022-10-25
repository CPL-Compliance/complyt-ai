package com.complyt.v1.controllers;

import com.complyt.ComplytApplication;
import com.complyt.domain.customer.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.customer.CustomerTypeDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.client.WebClient;
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

    @MockBean
    private CustomerFacade customerFacade;

    @Autowired
    private WebTestClient webTestClient;

    Customer customer;

    CustomerDto customerDto;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        customerDto = new CustomerDto(id, externalId, name, address, CustomerTypeDto.RETAIL);
        customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
    }

    @Test
    void initController_NullFacadeInstanceGiven_ThrowsNullPointerException() {
        // Given
        CustomerFacade facade = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            CustomerController controller = new CustomerController(facade);
        });

        assertEquals(nullPointerException.getMessage(), "customerfacade is marked non-null but is null");
    }

    @Test
    void update_NewCustomerCreated_SavesCustomer() {
        // Given
        when(customerFacade.upsert(customer)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/" + customer.getExternalId())
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
    void create_NewCustomerCreated_SavesCustomer() {
        // Given
        when(customerFacade.save(customer)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
                        .build())
                .bodyValue(customerDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(CustomerDto.class)
                .value(customerItem -> customerItem, equalTo(customerDto));
    }

    @Test
    void create_CreateFails_Returns5xxServerError() {
        // Given
        when(customerFacade.save(customer)).thenThrow(OperationFailedException.class);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
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
        List<Customer> customersFoundByName = new ArrayList<Customer>() {{
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

        List<Customer> allCustomers = new ArrayList<Customer>() {{
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
}