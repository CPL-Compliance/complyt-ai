package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.repositories.ClientRepository;
import com.complyt.repositories.CustomerRepository;
import com.complyt.repositories.OrderRepository;
import com.complyt.services.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        items.add(new Item(1000,3, 3000, "description","name","taxCode"));
        order = new Order(id, externalId, items, billingAddress,shippingAddress,customerId, null, OrderStatus.ACTIVE);
        ClientService clientService = new ClientServiceImpl(new ClientRepository());
    }

    @Test
    void initFacade_NullOrderServiceInstanceGiven_ThrowsNullPointerException(){
        // Given
        orderService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderFacade facade = new OrderFacade(orderService,salesTaxService);
        });

        assertEquals(nullPointerException.getMessage(), "orderService is marked non-null but is null");
    }

    @Test
    void initFacade_NullSalesTaxServiceInstanceGiven_ThrowsNullPointerException(){
        // Given
        salesTaxService = null;

        // When

        // Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            OrderFacade facade = new OrderFacade(orderService,salesTaxService);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxService is marked non-null but is null");
    }

    @Test
    public void saveOrder_OrderSaved_OrderReturned(){
        // Given

        // When
        when(orderService.save(order)).thenReturn(Mono.just(order));
        Mono<Order> monoOrder = orderFacade.save(order);
        Order returnedOrder = monoOrder.block();


        // Then
        assertNotNull(returnedOrder);
        assertEquals(order,returnedOrder);
    }

    @Test
    void upsertOrder_OrderInserted_OrderReturned() {
        // Given

        // When
        when(orderService.upsert(order)).thenReturn(Mono.just(order));
        Order returnedOrder = orderFacade.upsert(order).block();

        // Then
        assertNotNull(returnedOrder);
        assertEquals(order,returnedOrder);
    }

    @Test
    void addOrderToClient_OrderAddedToClient_OrderReturned() {
        // Given

        // When
        when(orderService.upsert(order)).thenReturn(Mono.just(order));
        Order returnedOrder = orderFacade.upsert(order).block();

        // Then
        assertNotNull(returnedOrder);
        assertEquals(order,returnedOrder);
    }


    @Test
    void getOrderByExternalId_OrderFound_OrderReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(id);

        // When
        when(orderService.findByExternalId(id)).thenReturn(Mono.just(orderToSearchFor));
        Order returnedCustomer = orderFacade.findByExternalId(id).block();

        // Then
        assertNotNull(returnedCustomer);
        assertEquals(returnedCustomer.getExternalId(),id);
        assertEquals(returnedCustomer,orderToSearchFor);
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
        List<Order> returnedCustomers = orderFacade.getAll().collectList().block();

        // Then
        assertNotNull(returnedCustomers);
        assertEquals(returnedCustomers.size(),2);
    }

    @Test
    void updateSalesTaxSync_ValidExternalIdGiven_UpdatesOrder(){
        // Given
        String externalId = order.getExternalId();
        SalesTax salesTax = new SalesTax(
                new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f, 0.5f),
                1000);
        Order orderWithSalesTax = order.withSalesTax(salesTax);

        // When
        when(orderService.findByExternalIdSync(externalId)).thenReturn(order);
        when(salesTaxService.getSalesTaxSync(order.getShippingAddress(),order.getItems())).thenReturn(salesTax);
        when(orderService.updateSync(orderWithSalesTax)).thenReturn(orderWithSalesTax);

        Order updatedOrder = orderFacade.updateSalesTaxSync(externalId);

        // Then
        assertNotNull(updatedOrder);
        assertEquals(updatedOrder,orderWithSalesTax);
    }

    @Test
    void markAsCancelled_orderIdGiven_ChangesOrderStatus(){
        // Given
        String orderId = order.getId();
        Order cancelledOrder = order.withOrderStatus(OrderStatus.CANCELLED);

        // When
        when(orderService.markAsCancelled(orderId)).thenReturn(Mono.just(cancelledOrder));
        Mono<Order> orderWithCancelledStatus = orderFacade.markAsCancelled(orderId);

        // Then
        assertNotNull(orderWithCancelledStatus);
        assertEquals(orderWithCancelledStatus.block(),cancelledOrder);

    }
}