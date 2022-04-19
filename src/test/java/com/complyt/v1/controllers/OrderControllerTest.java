package com.complyt.v1.controllers;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.facades.OrderFacade;

import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@WebFluxTest(OrderController.class)
@Import(JacksonConfig.class)
public class OrderControllerTest {

    @MockBean
    private OrderFacade orderFacade;

    @Autowired
    private WebTestClient webTestClient;

    Order orderWithId;

    OrderDto orderDto;

    @BeforeEach
    void cleanUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item("price", "quantity", "description", "name", "taxCode"));
            }
        };

        orderWithId = new Order(id, externalId, items, billingAddress, shippingAddress, customerId);
        orderDto = OrderMapper.INSTANCE.orderToOrderDto(orderWithId);
    }

    @Test
    void update_OrderCreated() {
        // Given

        Order orderNoId = orderWithId.withId(null);
        when(orderFacade.upsert(orderNoId)).thenReturn(Mono.just(orderWithId));
        System.out.println(orderDto);
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
//                .expectBody(OrderDto.class)
//                .value(orderDtoItem -> orderDtoItem.getCustomerId(), equalTo(orderDto.getCustomerId()));
    }

    @Test
    void update_UpdateFails_Returns5xxServerError() {
        // Given
        when(orderFacade.upsert(orderWithId)).thenThrow(OperationFailedException.class);

        // When + Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL)
                        .build())
                .bodyValue(orderWithId)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void getOrderByExternalId_FindsOrder_ReturnsOrder() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.just(orderWithId));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/findByExternalId")
                        .queryParam("externalId", externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
//                .expectBody(OrderDto.class)
//                .value(orderItem -> orderItem,equalTo(orderDto));
    }

    @Test
    void getCustomerByExternalId_FindsCustomer_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/findByExternalId")
                        .queryParam("externalId", externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void getAllOrders_ReturnsAllOrdersFound() {
        // Given
        when(orderFacade.getAllOrders()).thenReturn(Flux.fromIterable(new ArrayList<>()));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/all")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }
}

