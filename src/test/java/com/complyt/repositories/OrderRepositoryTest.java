package com.complyt.repositories;

import com.complyt.config.SecurityConfigMockTest;
import com.complyt.domain.*;
import com.complyt.domain.CustomerType;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.security.User;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@Import(SecurityConfigMockTest.class)
class OrderRepositoryTest {
    @InjectMocks
    OrderRepository orderRepository;

    @Mock
    ReactiveMongoTemplate reactiveMongoTemplate;

    Order order;

    Customer customer;

    User user;

    @BeforeEach
    void setUp() throws Exception {
        ObjectId clientId = new ObjectId("507f191e810c19729de860ea");
        user = User.builder().username("user").password("password").clientId(clientId).build();

        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f);
        items.add(new Item(2000, 4, 8000, "description", "name", "taxCode",null,salesTaxRate,false,0,TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE));
        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId,  null,null);
        customer = new Customer(customerId.toString(), externalId, "customer", shippingAddress,clientId,CustomerType.RETAIL);
    }

    @Test
    void init_NullReactiveMongoTemplateGiven_ThrowsException() {
        // Given
        reactiveMongoTemplate = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderRepository orderRepository = new OrderRepository(reactiveMongoTemplate);
        });

        assertEquals(nullPointerException.getMessage(), "reactiveMongoTemplate is marked non-null but is null");
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByExternalId_FindsOrder_ReturnsOrder() {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(order.getExternalId()).and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.just(order));
        when(reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        Mono<Order> orderMono = orderRepository.findByExternalId(order.getExternalId());

        // Then
        StepVerifier.create(orderMono).expectNext(order.withCustomer(customer)).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findOneById_IdDoesNotExist_ReturnsNull() {
        // Given
        Query query = Query.query(Criteria.where("_id").is(order.getId())
                .and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.just(order));
        when(reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        Mono<Order> orderMono = orderRepository.findById(order.getId());

        // Then
        StepVerifier.create(orderMono).expectNext(order.withCustomer(customer)).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findByExternalId_ExternalIdExists_ReturnsOneOrder() {
        // Given
        Query query = Query.query(Criteria.where("externalId").is(order.getExternalId()).and("clientId").is(user.getClientId()));

        // When
        when(reactiveMongoTemplate.findOne(query, Order.class)).thenReturn(Mono.just(order));
        when(reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));

        Mono<Order> orderMono = orderRepository.findByExternalId(order.getExternalId());

        // Then
        StepVerifier.create(orderMono).expectNext(order.withCustomer(customer)).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void insertAll_InsertsTwoOrders_ReturnsTwoOrders() {
        // Given
        String id = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(id);
        List<Order> allOrders = new ArrayList<>();
        allOrders.add(order);
        allOrders.add(secondOrder);

        // When
        when(reactiveMongoTemplate.insertAll(allOrders)).thenReturn(Flux.fromIterable(allOrders));
        when(reactiveMongoTemplate.findById(order.getCustomerId(),Customer.class)).thenReturn(Mono.just(customer));
        when(reactiveMongoTemplate.findById(secondOrder.getCustomerId(),Customer.class)).thenReturn(Mono.just(customer));
        Flux<Order> orderFlux = orderRepository.saveAll(allOrders);

        // Then
        StepVerifier.create(orderFlux).expectNextCount(2).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void saveOrder_OrderSaved_OrderReturned() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Order newOrder = order.withExternalId(id).withCustomer(customer);

        // When
        when(reactiveMongoTemplate.save(order)).thenReturn(Mono.just(newOrder));
        when(reactiveMongoTemplate.findById(newOrder.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        Mono<Order> orderMono = orderRepository.save(order);

        // Then
        StepVerifier.create(orderMono).expectNext(newOrder).verifyComplete();
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

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findAll_twoOrdersMatch_returnsTwoOrders() {
        // Given
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca89");
        Order secondOrder = order.withExternalId(externalId).withCustomerId(customerId);
        List<Order> allOrders = new ArrayList<Order>() {{
            add(order);
            add(secondOrder);
        }};
        Query query = Query.query(Criteria.where("clientId").is(user.getClientId()));

        //When
        when(reactiveMongoTemplate.find(query, Order.class)).thenReturn(Flux.fromIterable(allOrders));
        when(reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        when(reactiveMongoTemplate.findById(secondOrder.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));

        Flux<Order> orderFlux = orderRepository.findAll();

        //Then
        StepVerifier.create(orderFlux).expectNext(order.withCustomer(customer),secondOrder.withCustomer(customer)).verifyComplete();
    }

    @WithUserDetails(value = "test", userDetailsServiceBeanName = "userDetailsService")
    @Test
    void findAllByQuery_twoOrdersMatch_returnsTwoOrders() {
        // Given
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca89");
        Order secondOrder = order.withExternalId(externalId).withCustomerId(customerId);
        List<Order> allOrders = new ArrayList<Order>() {{
            add(order);
            add(secondOrder);
        }};
        LocalDateTime start = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(start).lte(end));

        //When
        when(reactiveMongoTemplate.find(query, Order.class)).thenReturn(Flux.fromIterable(allOrders));
        when(reactiveMongoTemplate.findById(order.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));
        when(reactiveMongoTemplate.findById(secondOrder.getCustomerId(), Customer.class)).thenReturn(Mono.just(customer));

        Flux<Order> orderFlux = orderRepository.findAllByQuery(query);

        //Then
        StepVerifier.create(orderFlux).expectNext(order.withCustomer(customer),secondOrder.withCustomer(customer)).verifyComplete();
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