package com.complyt.v1.controllers;

import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
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
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CustomerControllerTest {

    @InjectMocks
    CustomerController customerController;

    @Mock
    CustomerFacade customerFacade;

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
    void upsertCustomer_CustomerCreated() {
        // Given

        // When
        when(customerFacade.upsert(customer)).thenReturn(Mono.just(customer));
        CustomerDto returnedDto = customerController.upsertCustomer(customerDto).block();

        // Then
        Assertions.assertNotNull(returnedDto);
    }

    @Test
    void upsertCustomer_CustomerCreationFailed() {
        // Given

        // When
        when(customerFacade.upsert(customer)).thenReturn(Mono.empty());
        CustomerDto returnedDto = customerController.upsertCustomer(customerDto).block();

        // Then
        assertNull(returnedDto);
    }

    @Test
    void getCustomerByExternalId_CustomerFound_ReturnsCustomer(){
        // Given
        String id = UUID.randomUUID().toString();

        // When
        when(customerFacade.findByExternalId(id)).thenReturn(Mono.just(customer));
        ResponseEntity<CustomerDto> returnedDto = customerController.getCustomerByExternalId(id).block();

        // Then
        Assertions.assertNotNull(returnedDto);
        assertThat(returnedDto.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getCustomerByExternalId_CustomerNotFound_ReturnsError(){
        // Given
        String id = UUID.randomUUID().toString();

        // When
        when(customerFacade.findByExternalId(id)).thenReturn(Mono.empty());
        ResponseEntity<CustomerDto> returnedDto = customerController.getCustomerByExternalId(id).block();

        // Then
        Assertions.assertNotNull(returnedDto);
        assertThat(returnedDto.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getCustomerByName_NameExists_ReturnsOne() {
        // Given
        String name = "name";

        // When
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(Arrays.asList(customer)));
        List<CustomerDto> customers = customerController.getCustomerByName(name).collectList().block();

        // Then
        assertNotNull(customers);
        assertEquals(customers.size(),1);
    }

    @Test
    void getCustomerByName_NameDoesExist_ReturnsEmptyList() {
        // Given
        String name = "name";

        // When
        when(customerFacade.findByName(name)).thenReturn(Flux.fromIterable(new LinkedList<>()));
        List<CustomerDto> customers = customerController.getCustomerByName(name).collectList().block();

        // Then
        assertNotNull(customers);
        assertEquals(customers.size(),0);
    }


    @Test
    void getAllCustomers_ReturnsAllCustomers() {
        // Given
        Customer customer2 = customer.withName("newCustomer");
        List<Customer> allCustomers = new LinkedList<>();
        allCustomers.add(customer);
        allCustomers.add(customer2);

        // When
        when(customerFacade.getAllCustomers()).thenReturn(Flux.fromIterable(allCustomers));
        List<CustomerDto> customers = customerController.getAllCustomers().collectList().block();

        // Then
        assertNotNull(customers);
        assertEquals(customers.size(),2);
    }
}