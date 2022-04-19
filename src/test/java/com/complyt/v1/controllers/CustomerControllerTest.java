package com.complyt.v1.controllers;

import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.CustomerDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@WebFluxTest(CustomerController.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerTest {

//    @InjectMocks
//    private CustomerController customerController;

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
    void update_CustomerCreated() {
        // Given
        when(customerFacade.upsert(customer)).thenReturn(Mono.just(customer));

        // When + Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
                        .build())
                .bodyValue(customer)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

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
                .expectStatus().isOk();
    }

    @Test
    void getCustomerByExternalId_FindsCustomer_Returns4xxNotFound() {
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
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(Arrays.asList(customer)));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL)
                        .queryParam("name", name)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getAllCustomers_ReturnsAllCustomersFound() {
        // Given
        when(customerFacade.getAllCustomers()).thenReturn(Flux.fromIterable(new LinkedList<>()));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(CustomerController.BASE_URL + "/all")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
//
//    @Test
//    void getCustomerByExternalId_CustomerFound_ReturnsCustomer(){
//        // Given
//        String id = UUID.randomUUID().toString();
//
//        // When
//        when(customerFacade.findByExternalId(id)).thenReturn(Mono.just(customer));
//        ResponseEntity<CustomerDto> returnedDto = customerController.getCustomerByExternalId(id).block();
//
//        // Then
//        Assertions.assertNotNull(returnedDto);
//        assertThat(returnedDto.getStatusCode()).isEqualTo(HttpStatus.OK);
//    }
//
//    @Test
//    void getCustomerByExternalId_CustomerNotFound_ReturnsError(){
//        // Given
//        String id = UUID.randomUUID().toString();
//
//        // When
//        when(customerFacade.findByExternalId(id)).thenReturn(Mono.empty());
//        ResponseEntity<CustomerDto> returnedDto = customerController.getCustomerByExternalId(id).block();
//
//        // Then
//        Assertions.assertNotNull(returnedDto);
//        assertThat(returnedDto.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
//    }
//
//    @Test
//    void getCustomerByName_NameExists_ReturnsOne() {
//        // Given
//        String name = "name";
//
//        // When
//        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(Arrays.asList(customer)));
//        List<CustomerDto> customers = customerController.getCustomerByName(name).collectList().block();
//
//        // Then
//        assertNotNull(customers);
//        assertEquals(customers.size(),1);
//    }
//
//    @Test
//    void getCustomerByName_NameDoesNotExist_ReturnsEmptyList() {
//        // Given
//        String name = "name";
//
//        // When
//        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(new LinkedList<>()));
//        List<CustomerDto> customers = customerController.getCustomerByName(name).collectList().block();
//
//        // Then
//        assertNotNull(customers);
//        assertEquals(customers.size(),0);
//    }
//
//    @Test
//    void getAllCustomers_ReturnsAllCustomers() {
//        // Given
//        Customer customer2 = customer.withName("newCustomer");
//        List<Customer> allCustomers = new LinkedList<>();
//        allCustomers.add(customer);
//        allCustomers.add(customer2);
//
//        // When
//        when(customerFacade.getAllCustomers()).thenReturn(Flux.fromIterable(allCustomers));
//        List<CustomerDto> customers = customerController.getAllCustomers().collectList().block();
//
//        // Then
//        assertNotNull(customers);
//        assertEquals(customers.size(),2);
//    }
}