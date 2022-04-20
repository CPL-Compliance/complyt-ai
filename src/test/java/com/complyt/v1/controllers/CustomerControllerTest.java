package com.complyt.v1.controllers;

import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.facades.OrderFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.CustomerDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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

@ExtendWith(SpringExtension.class)
@WebFluxTest(CustomerController.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerTest {

    @MockBean
    private CustomerFacade customerFacade;

    @Autowired
    private WebTestClient webTestClient;

    Customer customer;
    CustomerDto customerDto;

    @BeforeAll
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        customerDto = new CustomerDto(id, externalId, name, address);
        customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
    }

    @Test
    void initController_NullFacadeInstanceGiven_ThrowsNullPointerException(){
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
    void updateCustomer_NewCustomerCreated_SavesCustomer() {
        // Given
        when(customerFacade.upsert(customer)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
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
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
                        .build())
                .bodyValue(customer)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getCustomerByExternalId_FindsCustomer_ReturnsCustomer() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/findByExternalId")
                        .queryParam("externalId", externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(CustomerDto.class)
                .value(customerItem -> customerItem, equalTo(customerDto));
    }

    @Test
    void getCustomerByExternalId_OperationFails_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(customerFacade.findByExternalId(externalId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/findByExternalId")
                        .queryParam("externalId", externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getCustomerByName_FindsCustomer_ReturnsCustomer() {
        // Given
        String name = "name";
        List<Customer> customersFoundByName = new ArrayList<Customer> (){{
            add(customer);
        }};
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(customersFoundByName));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
                        .queryParam("name", name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk().
                 expectBodyList(Customer.class)
                .value(customers -> customers , equalTo(customersFoundByName));
    }

    @Test
    void getAllCustomers_AllCustomersRetrieved_ReturnsAllCustomersFound() {
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
                        .path(CustomerController.BASE_URL + "/all")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Customer.class)
                .value(customers -> customers , equalTo(allCustomers));
    }
}