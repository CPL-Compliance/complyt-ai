package com.complyt.v1.controllers;

import com.complyt.domain.Order;
import com.complyt.facades.OrderFacade;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ItemDto;
import com.complyt.v1.model.OrderDto;
import org.bson.types.ObjectId;
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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)

public class OrderControllerTest {

    @InjectMocks
    OrderController orderController;

    @Mock
    OrderFacade orderFacade;

    Order order;
    OrderDto orderDto;

    @BeforeAll
    void setUp() {
        String externalId = UUID.randomUUID().toString();
        ObjectId orderId = new ObjectId("5399aba6e4b0ae375bfdca88");
        AddressDto billingAddress = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        AddressDto shippingAddress = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        List<ItemDto> items = new LinkedList<>();

        items.add(new ItemDto("price","quantity","description","name","taxCode"));

        orderDto = new OrderDto(externalId, items, billingAddress,shippingAddress,orderId);
        order = OrderMapper.INSTANCE.orderDtoToOrder(orderDto);
    }

    @Test
    void upsertOrder_OrderCreated() {
        // Given

        // When
        when(orderFacade.upsert(order)).thenReturn(Mono.just(order));
        ResponseEntity<OrderDto> returnedDto = orderController.update(orderDto).block();

        // Then
        Assertions.assertNotNull(returnedDto);
    }

    @Test
    void upsertOrder_OrderCreationFailed() {
        // Given

        // When
        when(orderFacade.upsert(order)).thenReturn(Mono.empty());
        ResponseEntity<OrderDto> returnedDto = orderController.update(orderDto).block();

        // Then
        Assertions.assertNull(returnedDto);
    }

    @Test
    void getOrderByExternalId_OrderFound_ReturnsOrder(){
        // Given
        String id = UUID.randomUUID().toString();

        // When
        when(orderFacade.findByExternalId(id)).thenReturn(Mono.just(order));
        ResponseEntity<OrderDto> returnedDto = orderController.getOrderByExternalId(id).block();

        // Then
        Assertions.assertNotNull(returnedDto);
        assertThat(returnedDto.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void getOrderByExternalId_OrderNotFound_ReturnsError(){
        // Given
        String id = UUID.randomUUID().toString();

        // When
        when(orderFacade.findByExternalId(id)).thenReturn(Mono.empty());
        ResponseEntity<OrderDto> returnedDto = orderController.getOrderByExternalId(id).block();

        // Then
        Assertions.assertNotNull(returnedDto);
        assertThat(returnedDto.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void getAllOrders_ReturnsAllOrders() {
        // Given
        String id = UUID.randomUUID().toString();
        Order order2 = order. withExternalId(id);
        List<Order> allOrders = new LinkedList<>();
        allOrders.add(order);
        allOrders.add(order2);

        // When
        when(orderFacade.getAllOrders()).thenReturn(Flux.fromIterable(allOrders));
        List<OrderDto> orders = orderController.getAllOrders().collectList().block();

        // Then
        Assertions.assertNotNull(orders);
        Assertions.assertEquals(orders.size(), 2);
    }

}
