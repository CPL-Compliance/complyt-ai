package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Customer;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.repositories.OrderRepository;
import org.bson.types.ObjectId;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderServiceImpl;

    @Mock
    OrderRepository orderRepository;

    Order order;

    @BeforeAll
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId orderId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new LinkedList<Item>();
        items.add(new Item("price","quantity","description","name","taxCode"));
        order = new Order(id, externalId, items, billingAddress,shippingAddress,orderId);
    }

    @Test
    void saveOrder_OrderSaved_OrderReturned() {
        // Given

        // When
        when(orderRepository.save(order)).thenReturn(order);
        Order returnedOrder = orderServiceImpl.save(order);

        // Then
        assertNotNull(returnedOrder);
        assertEquals(returnedOrder, order);

    }

    @Test
    void upsertOrder_OrderInserted_OrderReturned(){
        // Given

        // When
        when(orderRepository.upsert(order)).thenReturn(Mono.just(order));
        Order returnedOrder = orderServiceImpl.upsert(order).block();

        // Then
        assertNotNull(returnedOrder);
        assertEquals(returnedOrder, order);

    }

    @Test
    void findByExternalId_OrderFound_ReturnsOrder() {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(id);

        // When
        when(orderRepository.findByExternalId(id)).thenReturn(Mono.just(orderToSearchFor));
        Order returnedOrder = orderServiceImpl.findByExternalId(id).block();

        // Then
        assertNotNull(returnedOrder);
        assertEquals(returnedOrder, orderToSearchFor);
    }

    @Test
    void findById_OrderFound_ReturnsOrder() {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withId(id);

        // When
        when(orderRepository.findById(id)).thenReturn(Mono.just(orderToSearchFor));
        Order returnedOrder = orderServiceImpl.findById(id).block();

        // Then
        assertNotNull(returnedOrder);
        assertEquals(returnedOrder,orderToSearchFor);
    }

}