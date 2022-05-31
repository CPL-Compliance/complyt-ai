package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
public class OrderFacadeTest {

    @InjectMocks
    OrderFacade orderFacade;

    @Mock
    OrderService orderService;

    @Mock
    SalesTaxService salesTaxService;

    @Mock
    ProductClassificationService productClassificationService;

    Order order;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        items.add(new Item(1000, 3, 3000, "description", "name", "taxCode",
                null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f)
                ));
        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, OrderStatus.ACTIVE);
    }

    @Test
    void initFacade_NullOrderServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        orderService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderFacade facade = new OrderFacade(orderService, salesTaxService, productClassificationService);
        });

        assertEquals(nullPointerException.getMessage(), "orderService is marked non-null but is null");
    }

    @Test
    void initFacade_NullSalesTaxServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        salesTaxService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderFacade facade = new OrderFacade(orderService, salesTaxService, productClassificationService);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxService is marked non-null but is null");
    }

    @Test
    void initFacade_NullProductClassificationServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        productClassificationService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderFacade facade = new OrderFacade(orderService, salesTaxService, productClassificationService);
        });

        assertEquals(nullPointerException.getMessage(), "productClassificationService is marked non-null but is null");
    }

    @Test
    public void saveOrder_OrderSaved_OrderReturned() throws InterruptedException {
        // Given

        // When
        when(orderService.save(order)).thenReturn(Mono.just(order));
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        orderFacade.save(order)
                .subscribe(returnedOrder -> {
                    orderAtomicReference.set(returnedOrder);
                    countDownLatch.countDown();
                });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(order, orderAtomicReference.get());
    }

    @Test
    void upsertOrder_OrderInserted_OrderReturned() throws InterruptedException {
        // Given
        String externalId = order.getExternalId();
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(orderService.update(externalId, order)).thenReturn(Mono.just(order));
        orderFacade.update(externalId, order)
                .subscribe(returnedOrder -> {
                    orderAtomicReference.set(returnedOrder);
                    countDownLatch.countDown();
                });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(order, orderAtomicReference.get());
    }

    @Test
    void addOrderToClient_OrderAddedToClient_OrderReturned() throws InterruptedException {
        // Given
        String externalId = order.getExternalId();
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();

        // When
        when(orderService.update(externalId, order)).thenReturn(Mono.just(order));
        orderFacade.update(externalId, order).subscribe(returnedOrder -> {
            orderAtomicReference.set(returnedOrder);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(order, orderAtomicReference.get());
    }

    @Test
    void getOrderByExternalId_OrderFound_OrderReturned() throws InterruptedException {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(id);
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();

        // When
        when(orderService.findByExternalId(id)).thenReturn(Mono.just(orderToSearchFor));
        orderFacade.findByExternalId(id).subscribe(returnedOrder -> {
            orderAtomicReference.set(returnedOrder);
            countDownLatch.countDown();
        });

        // Then
        countDownLatch.await();
        assertNotNull(orderAtomicReference.get());
        assertEquals(orderAtomicReference.get().getExternalId(), id);
        assertEquals(orderToSearchFor, orderAtomicReference.get());
    }

    @Test
    void getAllOrders_AllOrdersRetrieved_ReturnsAllOrdersFound() {
        // Given
        String id = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(id);
        List<Order> allOrders = new ArrayList<>();
        allOrders.add(order);
        allOrders.add(secondOrder);

        // When
        when(orderService.findAll()).thenReturn(Flux.fromIterable(allOrders));
        Flux<Order> returnedCustomers = orderFacade.getAll();

        // Then
        StepVerifier.create(returnedCustomers).expectNextCount(2).verifyComplete();
    }

//    @Test
//    void updateSalesTax_ValidExternalIdGiven_UpdatesOrder() throws InterruptedException {
//        // Given
//        String externalId = order.getExternalId();
//        SalesTax salesTax = new SalesTax(1000);
//        Order orderWithSalesTax = order.withSalesTax(salesTax);
//        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        // When
//        when(orderService.findByExternalId(externalId)).thenReturn(Mono.just(order));
//        when(salesTaxService.findByAddress(order.getShippingAddress())).thenReturn(Mono.just(null));
//        when(salesTaxService.getRulesForItems(order.getItems(),null)).thenReturn(null);
//        when(salesTaxService.calculateSalesTax(order.getItems())).thenReturn(salesTax);
//        when(orderService.update(externalId, orderWithSalesTax)).thenReturn(Mono.just(orderWithSalesTax));
//
//        orderFacade.updateSalesTax(externalId).subscribe(returnedOrder -> {
//            orderAtomicReference.set(returnedOrder);
//            countDownLatch.countDown();
//        });

//        // Then
//        countDownLatch.await();
//        assertNotNull(orderAtomicReference.get());
//        assertEquals(orderWithSalesTax, orderAtomicReference.get());
//    }

    @Test
    void markAsCancelled_orderIdGiven_ChangesOrderStatus() {
        // Given
        String orderId = order.getId();
        Order cancelledOrder = order.withOrderStatus(OrderStatus.CANCELLED);

        // When
        when(orderService.markAsCancelled(orderId)).thenReturn(Mono.just(cancelledOrder));
        Mono<Order> orderWithCancelledStatus = orderFacade.markAsCancelled(orderId);

        // Then
        assertNotNull(orderWithCancelledStatus);
        assertEquals(orderWithCancelledStatus.block(), cancelledOrder);
    }
}