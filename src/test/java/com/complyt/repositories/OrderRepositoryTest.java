package com.complyt.repositories;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.mongodb.client.result.UpdateResult;
import org.bson.types.ObjectId;
import org.junit.Assert;
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
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
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
        List<Item> items = new LinkedList<Item>();
        items.add(new Item("price","quantity","description","name","taxCode"));
        order = new Order(id, externalId, items, billingAddress,shippingAddress,customerId);
    }

    @Test
    void save() {
    }

    @Test
    void findByExternalId_ExternalIdExists_ReturnsOneOrder(){
        // Given
        String orderExternalId = UUID.randomUUID().toString();
        Query query = Query.query(Criteria.where("externalId").is(orderExternalId));

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.just(new Order()));
        Mono<Order> monoOrder = orderRepository.findByExternalId(orderExternalId);
        Order order = monoOrder.block();

        // Then
        Assert.assertNotNull(order);
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
    void insertAll() {
    }

    @Test
    void findById() {
    }
}