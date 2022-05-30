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
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

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
                add(new Item(2000, 4, 8000, "description", "name", "taxCode"));
            }
        };

        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE);
    }

    @Test
    void saveOrder_OrderSaved_OrderReturned() {
        // Given

        // When
        when(orderRepository.save(order)).thenReturn(Mono.just(order));
        Mono<Order> orderMono = orderServiceImpl.save(order);

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
    }

    @Test
    void upsertOrder_OrderInserted_OrderReturned() {
        // Given
        String externalId = order.getExternalId();
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(orderRepository.findByExternalId(externalId)).thenReturn(Mono.just(order));
        when(orderRepository.save(order)).thenReturn(Mono.just(order));
        orderServiceImpl.upsert(externalId, order).subscribe(returnedOrder -> {
            orderAtomicReference.set(returnedOrder);
            countDownLatch.countDown();
        });

        // Then
        assertNotNull(orderAtomicReference.get());
        assertEquals(order, orderAtomicReference.get());
    }

    @Test
    void upsertOrder_NullGiven_NullPointerExceptionThrown() {
        // Given
        String externalId = "";
        Order order = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.upsert(externalId, order);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void findByExternalId_OrderFound_ReturnsOrder() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(id);
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(orderRepository.findByExternalId(id)).thenReturn(Mono.just(orderToSearchFor));
        orderServiceImpl.findByExternalId(id).subscribe(returnedOrder -> {
            orderAtomicReference.set(returnedOrder);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(orderToSearchFor, orderAtomicReference.get());
    }

    @Test
    void findByExternalId_NullExternalIdGiven_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.findByExternalId(null);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }

    @Test
    void findById_OrderFound_ReturnsOrder() {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withId(id);

        // When
        when(orderRepository.findById(id)).thenReturn(Mono.just(orderToSearchFor));
        Mono<Order> orderMono = orderServiceImpl.findById(id);

        // Then
        StepVerifier.create(orderMono).expectNext(orderToSearchFor).verifyComplete();
    }

    @Test
    void getAllOrders_AllOrdersRetrieved_ReturnsAllOrdersFound() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(externalId);

        //When
        when(orderRepository.findAll()).thenReturn(Flux.just(order, secondOrder));
        Flux<Order> orderFlux = orderServiceImpl.findAll();

        //Then
        StepVerifier.create(orderFlux).expectNext(order, secondOrder).verifyComplete();
    }

    @Test
    void update_NullOrderGiven_ThrowsException() {
        // Given
        String externalID = "";
        Order order = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.update(externalID, order);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void update_OrderUpdated_OrderReturned() throws InterruptedException {
        // Given
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        String externalId = order.getExternalId();

        // When
        when(orderRepository.findByExternalId(externalId)).thenReturn(Mono.just(order));
        when(orderRepository.save(order)).thenReturn(Mono.just(order));

        orderServiceImpl.update(externalId, order).subscribe(savedOrder -> {
            orderAtomicReference.set(savedOrder);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(order, orderAtomicReference.get());
    }

    @Test
    void updateSync_NullOrderGiven_ThrowsException() {
        // Given
        order = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.update("", order);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void markAsCancelled_ChangesOrdersStatus_ReturnsUpdatedOrder() throws InterruptedException {
        // Given
        Order cancelledOrderWithId = order.withOrderStatus(OrderStatus.CANCELLED).withId(order.getId());
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(orderRepository.findByExternalId(order.getExternalId())).thenReturn(Mono.just(order));
        when(orderRepository.save(cancelledOrderWithId)).thenReturn(Mono.just(cancelledOrderWithId));

        orderServiceImpl.markAsCancelled(order.getExternalId()).subscribe(returnedOrder -> {
            orderAtomicReference.set(returnedOrder);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(cancelledOrderWithId, orderAtomicReference.get());
    }

    @Test
    void saveOrders_NullGiven_ThrowsException() {
        // Given
        List<ObjectId> nullOrders = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderServiceImpl.save(nullOrders);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "orders is marked non-null but is null");
    }

    @Test
    void saveOrders_OrdersListGiven_ThrowsUnsupportedOperationException() {
        // Given
        List<ObjectId> orders = new ArrayList<ObjectId>() {{
            add(new ObjectId());
            add(new ObjectId());
        }};

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            orderServiceImpl.save(orders);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "save isn't implemented yet");
    }

    @Test
    void findByName_NameGiven_ThrowsUnsupportedOperationException() {
        // Given
        String name = "name";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            orderServiceImpl.findByName(name);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findByName isn't implemented");
    }

    @Test
    void findOneByName_NameGiven_ThrowsUnsupportedOperationException() {
        // Given
        String name = "name";

        // When
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            orderServiceImpl.findOneByName(name);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "findOneByName isn't implemented");
    }
}