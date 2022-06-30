package com.complyt.v1.controllers;

import com.complyt.config.JacksonConfig;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.facades.OrderFacade;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
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
@WebFluxTest(OrderController.class)
@ExtendWith(MockitoExtension.class)
@Import(JacksonConfig.class)
class OrderControllerTest {

    @MockBean
    private OrderFacade orderFacade;

    @Autowired
    private WebTestClient webTestClient;

    Order orderWithId;

    OrderDto orderDto;

    @BeforeEach
    void cleanUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        ObjectId clientId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),false,0
                    ));
            }
        };

        orderWithId = Order.builder()
                .id(id)
                .externalId(externalId)
                .items(items)
                .billingAddress(billingAddress)
                .shippingAddress(shippingAddress)
                .customerId(customerId)
                .orderStatus(OrderStatus.ACTIVE)
                .clientId(clientId)
                .build();

        orderDto = OrderMapper.INSTANCE.orderToOrderDto(orderWithId);
    }

    @WithUserDetails()
    @Test
    void initController_NullFacadeInstanceGiven_ThrowsNullPointerException() {
        // Given
        OrderFacade facade = null;
        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new OrderController(facade);
        });

        assertEquals(nullPointerException.getMessage(), "orderFacade is marked non-null but is null");
    }

    @WithUserDetails()
    @Test
    void update_NewOrderCreated_SavesOrder() {
        // Given
        when(orderFacade.update(orderDto.getExternalId(), OrderMapper.INSTANCE.orderDtoToOrder(orderDto)))
                .thenReturn(Mono.just(orderWithId));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + orderDto.getExternalId())
                        .build())
                .bodyValue(orderDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .value(orderDtoItem -> orderDtoItem, equalTo(orderDto));
    }

    @WithUserDetails()
    @Test
    void getOne_FindsOrder_ReturnsOrder() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.just(orderWithId));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .value(orderItem -> orderItem, equalTo(orderDto));
    }

    @WithUserDetails()
    @Test
    void getOne_OperationFails_Returns4xxNotFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        when(orderFacade.findByExternalId(externalId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @WithUserDetails()
    @Test
    void getAll_ExpectTwoOrders_ReturnsTwoOrders() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        OrderDto orderNoId = orderDto.withExternalId(firstId);
        OrderDto secondOrderNoId = orderDto.withExternalId(secondId);
        Order firstOrder = orderWithId.withExternalId(firstId);
        Order secondOrder = orderWithId.withExternalId(secondId);
        List<OrderDto> allOrdersWithNoId = new ArrayList<OrderDto>() {{
            add(orderNoId);
            add(secondOrderNoId);
        }};

        // When
        when(orderFacade.getAll()).thenReturn(Flux.just(firstOrder, secondOrder));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(OrderDto.class)
                .value(orderDtos -> orderDtos, equalTo(allOrdersWithNoId));
    }

    @WithUserDetails()
    @Test
    void updateSalesTax_UpdatesOrder_ReturnsStatus200() {
        // Given
        Order order = OrderMapper.INSTANCE.orderDtoToOrder(orderDto);

        // When + Then
        when(orderFacade.findByExternalId(order.getExternalId())).thenReturn(Mono.just(orderWithId));
        when(orderFacade.createOrderWithSalesTax(order)).thenReturn(Mono.just(orderWithId));
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL)
                        .build())
                .bodyValue(orderDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(OrderDto.class)
                .value(returnedOrderDto -> returnedOrderDto, equalTo(orderDto));

    }

    @WithUserDetails()
    @Test
    void markAsCancelled_CancelsOrder_OrderStatusChanges() {
        // Given
        Order cancelledOrdered = orderWithId.withOrderStatus(OrderStatus.CANCELLED);

        // When + Then
        when(orderFacade.markAsCancelled(orderWithId.getExternalId())).thenReturn(Mono.just(cancelledOrdered));
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(OrderController.BASE_URL + "/" + orderWithId.getExternalId())
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }
}