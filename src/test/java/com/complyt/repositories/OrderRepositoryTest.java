package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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
class OrderRepositoryTest {
    @InjectMocks
    OrderRepository orderRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Order order;

    @BeforeAll
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item("price","quantity","description","name","taxCode"));
        order = new Order(id, externalId, items, billingAddress,shippingAddress,customerId,null, OrderStatus.ACTIVE);
    }

    @Test
    void save() {
    }

    @Test
    void findOneById_IdDoesNotExist_ReturnsNull(){
        // Given
        Query query = Query.query(Criteria.where("_id").is(order.getExternalId()));

        // When
        when(reactiveMongoTemplate.findOne(query,Order.class)).thenReturn(null);
        Mono<Order> monoOrder = orderRepository.findById(order.getExternalId());

        // Then
        assertNull(monoOrder);
    }

    @Test
    void findByExternalId_ExternalIdExists_ReturnsOneOrder(){
        // Given
        String orderExternalId = UUID.randomUUID().toString();
        Query query = Query.query(Criteria.where("externalId").is(orderExternalId));

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.just(order.withExternalId(orderExternalId)));
        Mono<Order> monoOrder = orderRepository.findByExternalId(orderExternalId);
        Order order = monoOrder.block();

        // Then
        assertNotNull(order);
    }

    @Test
    void upsert_ExternalIdExists_UpdateExistingOrder(){
        // Given

        Address newBillingAddress = new Address("newCity","newCountry","newCounty","newState","newStreet","newZip");
        Order existingOrderWithNewAddress = order.withBillingAddress(newBillingAddress);
        Query query = Query.query(Criteria.where("externalId").is(existingOrderWithNewAddress.getExternalId()));

        Update update = new Update()
                .set("externalId", order.getExternalId())
                .set("billingAddress", newBillingAddress)
                .set("shippingAddress", order.getShippingAddress())
                .set("customerId", order.getCustomerId())
                .set("items", order.getItems());
        UpdateResult expectedResult = UpdateResult.acknowledged(1,null,null);

        // When
        when(reactiveMongoTemplate.upsert(query,update,Order.class)).thenReturn(Mono.just(expectedResult));
        when(reactiveMongoTemplate.findOne(query,Order.class)).thenReturn(Mono.just(existingOrderWithNewAddress));

        Mono<Order> monoOrder = orderRepository.upsert(existingOrderWithNewAddress);
        Order updatedOrder = monoOrder.block();

        // Then
        assertNotNull(updatedOrder);
        Assertions.assertEquals(existingOrderWithNewAddress,updatedOrder);
        Assertions.assertEquals(newBillingAddress,updatedOrder.getBillingAddress());
    }

    @Test
    void upsert_ExternalIdDoesNotExist_InsertNewOrder(){
        // Given
        String externalId = UUID.randomUUID().toString();
        Order newOrder = order.withExternalId(externalId);
        Query query = Query.query(Criteria.where("externalId").is(externalId));

        Update update = new Update()
                .set("externalId", externalId)
                .set("billingAddress", order.getBillingAddress())
                .set("shippingAddress", order.getShippingAddress())
                .set("customerId", order.getCustomerId())
                .set("items", order.getItems());
        UpdateResult expectedResult = UpdateResult.acknowledged(1,null,null);

        // When
        when(reactiveMongoTemplate.upsert(query,update,Order.class)).thenReturn(Mono.just(expectedResult));
        when(reactiveMongoTemplate.findOne(query,Order.class)).thenReturn(Mono.just(newOrder));

        Mono<Order> monoOrder = orderRepository.upsert(newOrder);
        Order insertedOrder = monoOrder.block();

        // Then
        assertNotNull(insertedOrder);
        Assertions.assertEquals(newOrder,insertedOrder);
    }

    @Test
    void upsert_UpsertNotAcknowledgedExist_ThrowsAnError() {
        // Given

        Query query = Query.query(Criteria.where("externalId").is(order.getExternalId()));

        Update update = new Update()
                .set("externalId", order.getExternalId())
                .set("billingAddress", order.getBillingAddress())
                .set("shippingAddress", order.getShippingAddress())
                .set("customerId", order.getCustomerId())
                .set("items", order.getItems());
        UpdateResult expectedResult = UpdateResult.unacknowledged();

        // When
        when(reactiveMongoTemplate.upsert(query,update, Order.class)).thenReturn(Mono.just(expectedResult));

        Exception exception = assertThrows(OperationFailedException.class, () -> {
            orderRepository.upsert(order).block();
        });

        String expectedMessage = "Could not update order";
        String actualMessage = exception.getMessage();

        // Then
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void insertAll_InsertsAll_ReturnsAll() {
        // Given
        String id = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(id);
        List<Order> allOrders = new ArrayList<>();
        allOrders.add(order);
        allOrders.add(secondOrder);

        // When
        when(reactiveMongoTemplate.insertAll(allOrders)).thenReturn(Flux.fromIterable(allOrders));
        List<Order> returnedOrders = orderRepository.insertAll(allOrders).collectList().block();

        // Then
        assertNotNull(returnedOrders);
        Assertions.assertEquals(returnedOrders.size(),2);
        Assertions.assertEquals(returnedOrders,allOrders);
    }

    @Test
    void saveOrder_OrderSaved_OrderReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Order newOrder = order.withExternalId(id);

        // When
        when(reactiveMongoTemplate.save(newOrder)).thenReturn(Mono.just(newOrder));
        Mono<Order> monoOrder = orderRepository.save(newOrder);
        Order returnedOrder = monoOrder.block();

        // Then
        assertNotNull(returnedOrder);
        Assertions.assertEquals(returnedOrder,newOrder);
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
    void findAll_returnsAll() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(externalId);
        List<Order> allOrders = new ArrayList<Order>() {{
            add(order);
            add(secondOrder);
        }};

        //When
        when(reactiveMongoTemplate.findAll(Order.class)).thenReturn(Flux.fromIterable(allOrders));
        List<Order> returnedOrders = orderRepository.findAll().collectList().block();

        //Then
        assertNotNull(returnedOrders);
        assertEquals(returnedOrders,allOrders);
    }

    @Test
    void upsert_NullGiven_ThrowsNullPointerException() {
        // Given
        Order order = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderRepository.upsert(order).block();
        });

        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void findById_NullGiven_ThrowsNullPointerException() {
        // Given
        String nullId = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderRepository.findById(nullId).block();
        });

        assertEquals(nullPointerException.getMessage(), "orderId is marked non-null but is null");
    }

    @Test
    void findByName_NameGiven_ThrowsUnsupportedOperationException(){
        // Given
        String name = "name";
        // When

        // Then
        UnsupportedOperationException nullPointerException = assertThrows(UnsupportedOperationException.class, () -> {
            orderRepository.findByName(name);
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
            orderRepository.findOneByName(name);
        });

        assertEquals(nullPointerException.getMessage(), "findOneByName isn't implemented");
    }
}