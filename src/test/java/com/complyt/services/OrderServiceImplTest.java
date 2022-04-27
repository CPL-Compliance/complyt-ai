package com.complyt.services;

import com.complyt.domain.Address;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
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
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item("price", "quantity", "description", "name", "taxCode"));
            }
        };

        order = new Order(id, externalId, items, billingAddress,shippingAddress,customerId,null);
    }

    @Test
    void saveOrder_OrderSaved_OrderReturned() {
        // Given

        // When
        when(orderRepository.save(order)).thenReturn(Mono.just(order));
        Mono<Order> monoOrder = orderServiceImpl.save(order);
        Order returnedOrder = monoOrder.block();

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
    void upsertOrder_NullGiven_NullPointerExceptionThrown(){
        // Given
        Order order = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.upsert(order);
        });

        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");

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

    @Test
    void getAllOrders_AllOrdersRetrieved_ReturnsAllOrdersFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(externalId);
        List<Order> allOrders = new ArrayList<Order>() {{
            add(order);
            add(secondOrder);
        }};

        //When
        when(orderRepository.findAll()).thenReturn(Flux.fromIterable(allOrders));
        List<Order> returnedOrders = orderServiceImpl.findAll().collectList().block();

        //Then
        assertNotNull(returnedOrders);
        assertEquals(returnedOrders,allOrders);
    }

    @Test
    void saveOrders_NullGiven_ThrowsException(){
        // Given
        List<ObjectId> nullOrders = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.save(nullOrders);
        });

        assertEquals(nullPointerException.getMessage(), "orders is marked non-null but is null");

    }

    @Test
    void saveOrders_OrdersListGiven_ThrowsUnsupportedOperationException(){
        // Given
        List<ObjectId> orders = new ArrayList<ObjectId>(){{
            add(new ObjectId());
            add(new ObjectId());
        }};

        // When

        // Then
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            orderServiceImpl.save(orders);
        });

        assertEquals(nullPointerException.getMessage(), "save isn't implemented yet");
    }

    @Test
    void findByName_NameGiven_ThrowsUnsupportedOperationException(){
        // Given
        String name = "name";
        // When

        // Then
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            orderServiceImpl.findByName(name);
        });

        assertEquals(nullPointerException.getMessage(), "findByName isn't implemented");
    }

    @Test
    void findOneByName_NameGiven_ThrowsUnsupportedOperationException(){
        // Given
        String name = "name";
        // When

        // Then
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            orderServiceImpl.findOneByName(name);
        });

        assertEquals(nullPointerException.getMessage(), "findOneByName isn't implemented");
    }
}