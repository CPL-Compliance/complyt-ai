package com.complyt.facades;

import com.complyt.domain.*;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import com.complyt.services.nexus.NexusService;
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

    @Mock
    NexusService nexusService;

    Order order;
    Order orderNoId;

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
                null, null, false, 0, TangibleCategory.NON_TANGIBLE, TaxableCategory.NOT_TAXABLE
        ));
        order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, OrderStatus.ACTIVE, clientId, null, null);
        orderNoId = order.withId(null);
    }

    private Order createOrderWithProductClassificationData() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();

        Item item = order.getItems().get(0)
                .withTaxableCategory(TaxableCategory.TAXABLE)
                .withTangibleCategory(TangibleCategory.TANGIBLE)
                .withJurisdictionalSalesTaxRules(rules);

        List<Item> modifiedItems = new ArrayList<Item>() {{add(item);}};
        return orderNoId.withItems(modifiedItems);

    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California","CA",true,
                false,CalculationType.FIXED,"description",0,null);
    }

    private SalesTaxTracking createSalesTaxTrackingWithoutNexusEstablished() {
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false,null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(false,null);

        State state = new State("CA","02","California");
        return new SalesTaxTracking(UUID.randomUUID().toString(),state,new ObjectId(),
                true,physicalNexusTracker,economicNexusTracker);
    }

    private SalesTaxTracking createSalesTaxTrackingWithNexusEstablished() {
        SalesTaxTracking salesTaxTrackingWithNexus = createSalesTaxTrackingWithoutNexusEstablished()
                .withEconomicNexusTracker(new EconomicNexusTracker(true,new Date()));

        return salesTaxTrackingWithNexus;
    }

    private SalesTax createSalesTax() {
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f,0.1f,0.1f,0.1f,0.1f,0.5f);
        return new SalesTax(1000,salesTaxRate);
    }

    @Test
    void initFacade_NullOrderServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        orderService = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> new OrderFacade(orderService, salesTaxService, productClassificationService, nexusService));

        assertEquals(nullPointerException.getMessage(), "orderService is marked non-null but is null");
    }

    @Test
    void initFacade_NullProductClassificationServiceInstanceGiven_ThrowsNullPointerException() {
        // Given
        productClassificationService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class,
                () -> new OrderFacade(orderService, salesTaxService, productClassificationService, nexusService));

        assertEquals(nullPointerException.getMessage(), "productClassificationService is marked non-null but is null");
    }

    @Test
    public void saveOrder_NexusIsNotEstablished_OrderSavedAndReturned() throws InterruptedException {
        // Given
        Order orderWithClassificationData = createOrderWithProductClassificationData();
        Order orderWithClassificationDataAndId = orderWithClassificationData.withId(order.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithoutNexusEstablished();


        // When
        when(productClassificationService.getOrderWithRelevantProductClassificationData(orderNoId)).thenReturn(Mono.just(orderWithClassificationData));
        when(nexusService.findTrackingByState(orderWithClassificationData)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.hasNexus(salesTaxTracking)).thenReturn(false);
        when(orderService.save(orderWithClassificationData)).thenReturn(Mono.just(orderWithClassificationDataAndId));
        when(nexusService.calculate(orderWithClassificationDataAndId)).thenReturn(Mono.just(salesTaxTracking));

        Mono<Order> actualOrder = orderFacade.saveOrder(orderNoId);

        // Then
        StepVerifier.create(actualOrder).expectNext(orderWithClassificationData).verifyComplete();
    }

    @Test
    public void saveOrder_NexusIsEstablished_CalculatesSalesTaxAndReturnsOrder() throws InterruptedException {
        // Given
        SalesTax salesTax = createSalesTax();
        Order orderWithClassificationData = createOrderWithProductClassificationData();
        Order orderWithClassificationDataAndSalesTax = orderWithClassificationData.withSalesTax(salesTax);
        Order orderWithClassificationDataAndSalesTaxAndId = orderWithClassificationDataAndSalesTax.withId(order.getId());
        SalesTaxTracking salesTaxTracking = createSalesTaxTrackingWithNexusEstablished();


        // When
        when(productClassificationService.getOrderWithRelevantProductClassificationData(orderNoId)).thenReturn(Mono.just(orderWithClassificationData));
        when(nexusService.findTrackingByState(orderWithClassificationData)).thenReturn(Mono.just(salesTaxTracking));
        when(nexusService.hasNexus(salesTaxTracking)).thenReturn(true);
        when(salesTaxService.handleSalesTaxCalculation(orderWithClassificationData,salesTaxTracking)).thenReturn(Mono.just(orderWithClassificationDataAndSalesTax));
        when(orderService.save(orderWithClassificationDataAndSalesTax)).thenReturn(Mono.just(orderWithClassificationDataAndSalesTaxAndId));

        Mono<Order> actualOrder = orderFacade.saveOrder(orderNoId);

        // Then
        StepVerifier.create(actualOrder).expectNext(orderWithClassificationDataAndSalesTaxAndId).verifyComplete();
    }

//    @Test
//    void updateOrder_OrderInserted_OrderReturned() throws InterruptedException {
//        // Given
//        String externalId = order.getExternalId();
//        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        // When
//        when(orderService.update(externalId, order)).thenReturn(Mono.just(order));
//
//        orderFacade.updateIfModified(externalId, order)
//                .subscribe(returnedOrder -> {
//                    orderAtomicReference.set(returnedOrder);
//                    countDownLatch.countDown();
//                });
//    }
//        // Then
//        countDownLatch.await();
//        assertNotNull(orderAtomicReference.get());
//        assertEquals(order, orderAtomicReference.get());
//    }
//
//    @Test
//    void upsertOrder_Orderupserted_OrderReturned() throws InterruptedException {
//        // Given
//        String externalId = order.getExternalId();
//        AtomicReference<Order> orderAtomicReference = new AtomicReference<>();
//        CountDownLatch countDownLatch = new CountDownLatch(1);
//
//        // When
//        when(orderService.upsert(externalId, order)).thenReturn(Mono.just(order));
//        orderFacade.upsert(externalId, order)
//                .subscribe(returnedOrder -> {
//                    orderAtomicReference.set(returnedOrder);
//                    countDownLatch.countDown();
//                });
//
//        // Then
//        countDownLatch.await();
//        assertNotNull(orderAtomicReference.get());
//        assertEquals(order, orderAtomicReference.get());
//    }
//

    @Test
    void getOrderByExternalId_OrderFound_OrderReturned() throws InterruptedException {
        // Given
        String externalId = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(externalId);

        // When
        when(orderService.findByExternalId(externalId)).thenReturn(Mono.just(orderToSearchFor));
        Mono<Order> returnedOrder = orderFacade.findByExternalId(externalId);

        // Then
        StepVerifier.create(returnedOrder).expectNext(orderToSearchFor).verifyComplete();
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
    void updateIfModified_OrderNotModified_ReturnsSameOrder() {
        // Given

        // When
        when(orderService.findByExternalId(order.getExternalId())).thenReturn(Mono.just(order));
        Mono<Order> orderMono = orderFacade.updateIfModified(order.getExternalId(), order);

        // Then
        StepVerifier.create(orderMono).expectNext(order).verifyComplete();
    }

    @Test
    void updateIfModified_NullExternalidPassed_ThrowsException() {
        // Given
        String nullExternalId = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> orderFacade.updateIfModified(nullExternalId, order));

        // Then
        assertEquals(nullPointerException.getMessage(), "externalId is marked non-null but is null");
    }

    //
//    @Test
//    void updateIfModified_OrderModified_UpdatesOrder() {
//        // Given
//        Address newShippingAddress = order.getShippingAddress().withState("newState");
//        Order orderWithNewAddress = order.withShippingAddress(newShippingAddress);
//
//        SalesTax salesTax = new SalesTax(100,new SalesTaxRate(0,0,0,0,0,0));
//        Order newOrderWithSalesTax = orderWithNewAddress.withSalesTax(salesTax);
//        Order modifiedOrder = getOrderWithRelevantProductClassificationData();
//
//        // When
//        when(orderService.findByExternalId(order.getExternalId())).thenReturn(Mono.just(order));
//        when(productClassificationService.injectJurisdictionalRules(order,new OrderJurisdictionalRulesInjector(orderWithNewAddress)))
//                .thenReturn(Mono.just(orderWithNewAddress));
//        when(orderService.calculate(orderWithNewAddress)).thenReturn(Mono.just(newOrderWithSalesTax));
//        when(orderService.update(newOrderWithSalesTax.getExternalId(),newOrderWithSalesTax)).thenReturn(Mono.just(newOrderWithSalesTax));
//        Mono<Order> orderMono = orderFacade.updateIfModified(orderWithNewAddress.getExternalId(),orderWithNewAddress);
//
//        // Then
//        StepVerifier.create(orderMono).expectNext(newOrderWithSalesTax).verifyComplete();
//    }
//
//    @Test
//    void saveOrder_OrderSavedWithSalesTax_OrderReturned() {
//        // Given
//        SalesTaxRate salesTaxRate = new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
//        SalesTax salesTax = new SalesTax(10,salesTaxRate);
//        Order orderWithSalesTax = order.withSalesTax(salesTax);
//        OrderJurisdictionalRulesInjector orderProductClassificationInjector = new OrderJurisdictionalRulesInjector(order);
//
//        // When
//        when(productClassificationService.setJurisdictionalRules(orderProductClassificationInjector)).thenReturn(Mono.just(order));
//        when(orderService.calculate(order)).thenReturn(Mono.just(orderWithSalesTax));
//        when(orderService.save(orderWithSalesTax)).thenReturn(Mono.just(orderWithSalesTax));
//
//        Mono<Order> monoOrder = orderFacade.saveOrder(order);
//
//        // Then
//        StepVerifier.create(monoOrder).expectNext(orderWithSalesTax).verifyComplete();
//    }
//
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
