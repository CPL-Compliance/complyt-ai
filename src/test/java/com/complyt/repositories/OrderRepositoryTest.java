package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.security.User;
import org.bson.types.ObjectId;
import org.junit.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.test.context.TestSecurityContextHolder;
import org.springframework.security.test.context.support.ReactorContextTestExecutionListener;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
//@TestExecutionListeners(WithSecurityContextTestExecutionListener.class)
class OrderRepositoryTest {
//    @Mock
//    private Authentication authentication;

//    private TestExecutionListener reactorContextTestExecutionListener = new ReactorContextTestExecutionListener();

    @InjectMocks
    OrderRepository orderRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Order order;

    User user;

    @BeforeEach
    void setUp() throws Exception {
        ObjectId clientId = new ObjectId("1234");
        user = User.builder().username("user").password("1234").clientId(clientId).build();

//        when(authentication.getPrincipal()).thenReturn(user);
//        TestSecurityContextHolder.setAuthentication(authentication);
//        reactorContextTestExecutionListener.beforeTestMethod(null);

        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(2000, 4, 8000, "description", "name", "taxCode"));
        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId);
    }

    @After
    public void tearDown() throws Exception {
//        reactorContextTestExecutionListener.afterTestMethod(null);
    }

    @Test
    void init_NullReactiveMongoTemplateGiven_ThrowsException() {
        // Given
        reactiveMongoTemplate = null;

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderRepository orderRepository = new OrderRepository(reactiveMongoTemplate);
        });

        assertEquals(nullPointerException.getMessage(), "reactiveMongoTemplate is marked non-null but is null");
    }

    @Test
    void findByExternalId_FindsOrder_ReturnsOrder() throws InterruptedException {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(order.getExternalId()));
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.just(order));
        Mono<Order> orderMono = orderRepository.findByExternalId(order.getExternalId());
//        orderRepository.findByExternalId(order.getExternalId()).subscribe(returnedOrder -> {
//            orderAtomicReference.set(returnedOrder);
//            countDownLatch.countDown();
//        });

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
//        countDownLatch.await();
//        assertNotNull(orderAtomicReference.get());
//        assertEquals(order, orderAtomicReference.get());
    }

    @Test
    void findOneById_IdDoesNotExist_ReturnsNull() throws InterruptedException {
        // Given
        Query query = Query.query(Criteria.where("_id").is(order.getId()));

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.empty());
        Mono<Order> orderMono = orderRepository.findById(order.getId());

        // Then
        StepVerifier.create(orderMono).expectNextCount(0).verifyComplete();
    }

    @Test
    void findByExternalId_ExternalIdExists_ReturnsOneOrder() {
        // Given
        String orderExternalId = UUID.randomUUID().toString();
//        Query query = Query.query(Criteria.where("externalId").is(orderExternalId));
//        Order returnedOrder = order.withExternalId(orderExternalId);
        Query query = Query.query(Criteria.where("externalId").is(order.getExternalId()).and("clientId").is(user.getClientId()));
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.just(order));
        Mono<Order> orderMono = orderRepository.findByExternalId(orderExternalId);

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
    }

    @Test
    void insertAll_Inserts2Orders_Returns2Orders() {
        // Given
        String id = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(id);
        List<Order> allOrders = new ArrayList<>();
        allOrders.add(order);
        allOrders.add(secondOrder);

        // When
        when(reactiveMongoTemplate.insertAll(allOrders)).thenReturn(Flux.fromIterable(allOrders));
        Flux<Order> orderFlux = orderRepository.saveAll(allOrders);

        // Then
        StepVerifier.create(orderFlux).expectNextCount(2).verifyComplete();
    }

    @Test
    void saveOrder_OrderSaved_OrderReturned() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Order newOrder = order.withExternalId(id);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();

        // When
        when(reactiveMongoTemplate.save(order)).thenReturn(Mono.just(newOrder));
        orderRepository.save(order).subscribe(returnedOrder -> {
            orderAtomicReference.set(returnedOrder);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(newOrder, orderAtomicReference.get());
    }

    @Test
    void saveOrder_Null_ThrowsNullPointerException() {
        // Given
        Order order = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderRepository.save(order);
        });

        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void findAll_returns2Orders() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(externalId);
        List<Order> allOrders = new ArrayList<Order>() {{
            add(order);
            add(secondOrder);
        }};

        //When
        when(reactiveMongoTemplate.findAll(Order.class)).thenReturn(Flux.fromIterable(allOrders));
        Flux<Order> orderFlux = orderRepository.find();

        //Then
        StepVerifier.create(orderFlux).expectNextCount(2).verifyComplete();
    }

    @Test
    void findById_NullGiven_ThrowsNullPointerException() {
        // Given
        String nullId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderRepository.findById(nullId);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "orderId is marked non-null but is null");
    }
}