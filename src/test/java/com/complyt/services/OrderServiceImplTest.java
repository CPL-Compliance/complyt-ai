package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.repositories.OrderRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000,4,8000,"description","name","taxCode"));
            }
        };

        order = new Order(id, externalId, items, billingAddress,shippingAddress,customerId,null, OrderStatus.ACTIVE);
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
        when(orderRepository.upsertSync(order)).thenReturn(Mono.just(order));
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
    void findByExternalIdSync_OrderFound_ReturnsOrder(){
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(id);

        // When
        when(orderRepository.findByExternalIdSync(id)).thenReturn(orderToSearchFor);
        Order returnedOrder = orderServiceImpl.findByExternalIdSync(id);

        // Then
        assertNotNull(returnedOrder);
        assertEquals(returnedOrder, orderToSearchFor);

    }

    @Test
    void findByExternalIdSync_NullExternalIdGiven_ThrowsException(){
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.findByExternalIdSync(nullExternalId);
        });

        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
        
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
    void update_OrderUpdated_OrderReturned() {
        // Given

        // When
        when(orderRepository.update(order)).thenReturn(Mono.just(order));
        Mono<Order> monoOrder = orderServiceImpl.update(order);

        // Then
        assertNotNull(monoOrder);
        assertEquals(monoOrder.block(), order);

    }

    @Test
    void update_NullOrderGiven_ThrowsException() {
        // Given
        order = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.update(order);
        });

        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");

    }

    @Test
    void updateSync_OrderUpdated_OrderReturned() {
        // Given

        // When
        when(orderRepository.updateSync(order)).thenReturn(order);
        Order returnedOrder = orderServiceImpl.updateSync(order);

        // Then
        assertNotNull(returnedOrder);
        assertEquals(returnedOrder, order);

    }

    @Test
    void updateSync_NullOrderGiven_ThrowsException() {
        // Given
        order = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.updateSync(order);
        });

        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");

    }

    @Test
    void markAsCancelled_ChangesOrdersStatus_ReturnsUpdatedOrder() {
        // Given
        Order cancelledOrderWithId = order.withOrderStatus(OrderStatus.CANCELLED).withId(order.getId());

        // When
        when(orderRepository.findByExternalIdSync(order.getExternalId())).thenReturn(order);
        when(orderRepository.updateSync(cancelledOrderWithId)).thenReturn(cancelledOrderWithId);

        Mono<Order> updatedOrder = orderServiceImpl.markAsCancelled(order.getExternalId());

        //
        assertNotNull(updatedOrder);
        assertEquals(updatedOrder.block(), cancelledOrderWithId);
        assertEquals(updatedOrder.block().getId(),cancelledOrderWithId.getId());
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