package com.complyt.v1.controllers;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.facades.OrderFacade;

import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import org.bson.types.ObjectId;
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
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebFluxTest(OrderController.class)
public class OrderControllerTest {

    @MockBean
    private OrderFacade orderFacade;

    @MockBean
    private OrderMapper orderMapper;

    @Autowired
    private WebTestClient webTestClient;

    Order order;
    OrderDto orderDto;

    @BeforeAll
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new LinkedList<Item>();
        items.add(new Item("price","quantity","description","name","taxCode"));
        order = new Order(id, externalId, items, billingAddress,shippingAddress,customerId);
        orderDto = OrderMapper.INSTANCE.orderToOrderDto(order);
    }

    @Test
    void update_OrderCreated() {
        // Given
        when(orderMapper.INSTANCE.orderDtoToOrder(orderDto)).thenReturn(order);
        when(orderFacade.upsert(order)).thenReturn(Mono.just(order));

        // When + Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL)
                        .build())
                .bodyValue(orderDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

    }
//
//    @Test
//    void update_UpdateFails_Returns5xxServerError() {
//        // Given
//        when(orderFacade.upsert(order)).thenThrow(OperationFailedException.class);
//
//        // When + Then
//        webTestClient
//                .put()
//                .uri(uriBuilder -> uriBuilder
//                        .path(OrderController.BASE_URL)
//                        .build())
//                .bodyValue(order)
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().is5xxServerError();
//
//    }
//
//    @Test
//    void getCustomerByExternalId_FindsCustomer_ReturnsCustomer() {
//        // Given
//        String externalId = UUID.randomUUID().toString();
//        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.just(order));
//
//        // When + Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(OrderController.BASE_URL + "/findByExternalId")
//                        .queryParam("externalId", externalId)
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk();
//    }
//
//    @Test
//    void getCustomerByExternalId_FindsCustomer_Returns4xxNotFound() {
//        // Given
//        String externalId = UUID.randomUUID().toString();
//        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.empty());
//
//        // When + Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(OrderController.BASE_URL + "/findByExternalId")
//                        .queryParam("externalId", externalId)
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isNotFound();
//    }
//
//    @Test
//    void getCustomerByName_FindsCustomer_ReturnsCustomer() {
//        // Given
//        String name = "name";
//        when(orderFacade.findByName(name)).thenReturn(Flux.fromIterable(Arrays.asList(order)));
//
//        // When + Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(OrderController.BASE_URL)
//                        .queryParam("name", name)
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk();
//    }
//
//    @Test
//    void getAllCustomers_ReturnsAllCustomersFound() {
//        // Given
//        when(orderFacade.getAllCustomers()).thenReturn(Flux.fromIterable(new LinkedList<>()));
//
//        // When + Then
//        webTestClient
//                .get()
//                .uri(uriBuilder -> uriBuilder
//                        .path(OrderController.BASE_URL + "/all")
//                        .build())
//                .accept(MediaType.APPLICATION_JSON)
//                .exchange()
//                .expectStatus().isOk();
//    }
}







