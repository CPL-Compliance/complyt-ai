package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
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
        ObjectId clientId = new ObjectId();
        items.add(new Item(1000, 3, 3000, "description", "name", "C1S1",
                null, null,false,0
        ));
        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId);
    }

    @Test
    void initFacade_NullOrderServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        orderService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new OrderFacade(orderService, salesTaxService, productClassificationService);
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
            new OrderFacade(orderService, salesTaxService, productClassificationService);
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
    void updateOrder_OrderInserted_OrderReturned() throws InterruptedException {
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
    void upsertOrder_Orderupserted_OrderReturned() throws InterruptedException {
        // Given
        String externalId = order.getExternalId();
        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // When
        when(orderService.upsert(externalId, order)).thenReturn(Mono.just(order));
        orderFacade.upsert(externalId, order)
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
    void upsertOrder_NullExternalIdGiven_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderFacade.upsert(nullExternalId, order);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");

    }

    @Test
    void update_NullExternalIdGiven_ThrowsException() {
        // Given
        String externalId = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            orderFacade.update(externalId, order);
        });

        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
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

    @Test
    void updateSalesTax_ValidExternalIdGiven_UpdatesOrder() throws InterruptedException {
        // Given
        String externalId = order.getExternalId();
        FastTaxData fastTaxData = new FastTaxData();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
        SalesTax salesTax = new SalesTax(1000, salesTaxRate);
        String taxCode = "C1S1";

        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = new JurisdictionalSalesTaxRules("California",
                order.getShippingAddress().getState(), true, false, CalculationType.FIXED, "description", 0,null);
        Map<String, JurisdictionalSalesTaxRules> jurisdictionalSalesTaxRulesList = new HashMap<String, JurisdictionalSalesTaxRules>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};
        ProductClassification productClassification = new ProductClassification("id", taxCode, "description",
                "title", jurisdictionalSalesTaxRulesList);

        Item itemWithRule = order.getItems().get(0).withJurisdictionalSalesTaxRules(jurisdictionalSalesTaxRules);
        List<Item> itemsWithRules = new ArrayList<Item>() {{
            add(itemWithRule);
        }};

        Item itemWithRate = itemWithRule.withSalesTaxRate(salesTaxRate);
        List<Item> itemsWithRates = new ArrayList<Item>() {{
            add(itemWithRate);
        }};

        Order orderWithSalesTax = order.withSalesTax(salesTax).withItems(itemsWithRates);

        // When
        when(orderService.findByExternalId(externalId)).thenReturn(Mono.just(order));

        when(productClassificationService.findOneByTaxCode(taxCode)).thenReturn(Mono.just(productClassification));

        when(salesTaxService.findByAddress(order.getShippingAddress())).thenReturn(Mono.just(fastTaxData));

        when(salesTaxService.salesTaxDataToSalesTaxRate(fastTaxData)).thenReturn(salesTaxRate);

        when(salesTaxService.setSalesTaxRatesForItems(itemsWithRules, salesTaxRate)).thenReturn(itemsWithRates);

        when(salesTaxService.calculateSalesTaxAmount(itemsWithRates)).thenReturn(salesTax.getAmount());

        when(orderService.update(externalId, orderWithSalesTax)).thenReturn(Mono.just(orderWithSalesTax));

        Mono<Order> orderMono = orderFacade.updateSalesTax(externalId);

        // Then
        StepVerifier.create(orderMono).expectNext(orderWithSalesTax).verifyComplete();
    }

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

    @Test
    void getClassification_ClassificationFound_Classification_returned() {
        // Given
        String taxCode = "C1S1";
        ProductClassification productClassification = new ProductClassification("id", "C1S1", "description",
                "title", null);

        // When
        when(productClassificationService.findOneByTaxCode(taxCode)).thenReturn(Mono.just(productClassification));
        Mono<ProductClassification> productClassificationMono = orderFacade.getClassification(taxCode);

        // Then
        StepVerifier.create(productClassificationMono).expectNext(productClassification).verifyComplete();

    }

    @Test
    void getAll_findsAllOrdersWithClientId_ReturnsAllOrders() {
        // Given
        String anotherOrderId = UUID.randomUUID().toString();
        Order anotherOrderWithSameClientId = order.withId(anotherOrderId);
        List<Order> orders = new ArrayList<Order>() {{
            add(order);
            add(anotherOrderWithSameClientId);
        }};

        // When
        when(orderService.findAll()).thenReturn(Flux.fromIterable(orders));
        Flux<Order> orderFlux = orderFacade.getAll();

        // Then
        StepVerifier.create(orderFlux).expectNext(order, anotherOrderWithSameClientId).verifyComplete();

    }
}