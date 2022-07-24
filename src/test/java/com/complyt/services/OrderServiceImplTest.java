package com.complyt.services;

import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.repositories.OrderRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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
class OrderServiceImplTest {

    @InjectMocks
    OrderServiceImpl orderService;

    @Mock
    OrderRepository orderRepository;

    @Mock
    ProductClassificationServiceImpl productClassificationService;

    Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),false,0,TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
            }
        };

        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId,  null,null);
    }

    @Test
    void saveOrder_OrderSaved_OrderReturned() {
        // Given

        // When
        when(orderRepository.save(order)).thenReturn(Mono.just(order));
        Mono<Order> orderMono = orderService.save(order);

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
    }

    @Test
    void findByExternalId_OrderFound_ReturnsOrder() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(id);

        // When
        when(orderRepository.findByExternalId(id)).thenReturn(Mono.just(orderToSearchFor));
        Mono<Order> orderMono = orderService.findByExternalId(id);

        // Then
        StepVerifier.create(orderMono).expectNext(orderToSearchFor).verifyComplete();
    }

    @Test
    void findByExternalId_NullExternalIdGiven_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderService.findByExternalId(null);
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
        Mono<Order> orderMono = orderService.findById(id);

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
        Flux<Order> orderFlux = orderService.findAll();

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
            orderService.update(externalID, order);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void update_NullExternalIdGiven_ThrowsException() {
        // Given
        String externalID = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> orderService.update(externalID, order));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }


    @Test
    void update_OrderUpdated_OrderReturned() {
        // Given
        String externalId = order.getExternalId();

        // When
        when(orderRepository.findByExternalId(externalId)).thenReturn(Mono.just(order));
        when(orderRepository.save(order)).thenReturn(Mono.just(order));

        Mono<Order> orderMono = orderService.update(externalId, order);

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
    }

    @Test
    void updateSync_NullOrderGiven_ThrowsException() {
        // Given
        order = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderService.update("", order);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

    @Test
    void markAsCancelled_ChangesOrdersStatus_ReturnsUpdatedOrder() throws InterruptedException {
        // Given
        Order cancelledOrder = order.withOrderStatus(OrderStatus.CANCELLED);

        // When
        when(orderRepository.findByExternalId(order.getExternalId())).thenReturn(Mono.just(order));
        when(orderRepository.save(cancelledOrder)).thenReturn(Mono.just(cancelledOrder));

        Mono<Order> orderMono = orderService.markAsCancelled(order.getExternalId());

        // Then
        StepVerifier.create(orderMono).expectNext(cancelledOrder).verifyComplete();
    }

    @Test
    void find_findsAllOrdersWithClientId_ReturnsAllOrders() {
        // Given
        String anotherOrderId = UUID.randomUUID().toString();
        Order anotherOrderWithSameClientId = order.withId(anotherOrderId);
        List<Order> orders = new ArrayList<Order>() {{
            add(order);
            add(anotherOrderWithSameClientId);
        }};

        // When
        when(orderRepository.findAll()).thenReturn(Flux.fromIterable(orders));
        Flux<Order> orderFlux = orderService.findAll();

        // Then
        StepVerifier.create(orderFlux).expectNext(order,anotherOrderWithSameClientId).verifyComplete();
    }

    @Test
    void getOrdersByQuery_TwoOrdersMatch_returnsTwoOrders() {
        // Given
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId("5399aba6e4b0ae375bfdca89");
        Customer customer = new Customer(customerId.toString(), externalId, "customer", order.getShippingAddress(),new ObjectId(),CustomerType.RETAIL);

        Order orderWithCustomer = order.withCustomer(customer);
        Order secondOrderWithCustomer = order.withExternalId(externalId).withCustomerId(customerId).withCustomer(customer);

        List<Order> allOrders = new ArrayList<Order>() {{
            add(orderWithCustomer);
            add(secondOrderWithCustomer);
        }};
        LocalDateTime start = LocalDate.now().minusYears(1).atStartOfDay();
        LocalDateTime end = start.plusYears(1);
        Query query = Query.query(Criteria.where("externalTimeStamps.createdDate")
                .gte(start).lte(end));

        // When
        when(orderRepository.findAllByQuery(query)).thenReturn(Flux.fromIterable(allOrders));
        Flux<Order> orderFlux = orderService.getOrdersByQuery(query);

        // Then
        StepVerifier.create(orderFlux).expectNext(order.withCustomer(customer),secondOrderWithCustomer).verifyComplete();
    }

    @Test
    void getOrdersByQuery_NullQueryPassed_ThrowsException() {
        // Given
        Query nullQuery = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderService.getOrdersByQuery(nullQuery);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "query is marked non-null but is null");
    }

    @Test
    void createUpdateOrderFunction_NullOrderPassed_ThrowsException() {
        // Given
        String externalId = UUID.randomUUID().toString();
        Order nullOrder = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderService.update(externalId,nullOrder);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "order is marked non-null but is null");
    }

}