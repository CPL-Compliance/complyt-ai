package com.complyt.repositories;

import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class OrderRepositoryTest {
    @InjectMocks
    OrderRepository orderRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

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
    void insertAll() {
    }

    @Test
    void findById() {
    }
}