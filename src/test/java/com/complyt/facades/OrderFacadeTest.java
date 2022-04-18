package com.complyt.facades;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.services.ClientService;
import com.complyt.services.CustomerService;
import com.complyt.services.OrderService;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
    CustomerService customerService;

    @Mock
    ClientService clientService;

    Order order;

    @BeforeAll
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId orderId = new ObjectId("5399aba6e4b0ae375bfdca88");
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new LinkedList<Item>();
        items.add(new Item("price","quantity","description","name","taxCode"));
        order = new Order(id, externalId, items, billingAddress,shippingAddress,orderId);
    }

    @Test
    public void saveOrder_OrderSavedAndReturned(){
        // Given
        String name = "name";

        // When
        when(orderService.save(order)).thenReturn(order);
        Order returnedOrder = orderFacade.save(order);

        // Then
        assertNotNull(returnedOrder);
        assertEquals(order,returnedOrder);
    }

    @Test
    void upsertOrder_TheInsertedOrderReturned() {
        // Given

        // When
        when(orderService.upsert(order)).thenReturn(Mono.just(order));
        Order returnedOrder = orderFacade.upsert(order).block();

        // Then
        assertNotNull(returnedOrder);
    }

    @Test
    void addOrderToClient_OrderaddedToClient() {
        // Given

        // When
        when(orderService.upsert(order)).thenReturn(Mono.just(order));
        Order returnedOrder = orderFacade.upsert(order).block();

        // Then
        assertNotNull(returnedOrder);
    }


    @Test
    void getOrderByExternalId_OrderFoundAndReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Order orderToSearchFor = order.withExternalId(id);

        // When
        when(orderService.findByExternalId(id)).thenReturn(Mono.just(orderToSearchFor));
        Order returnedCustomer = orderFacade.findByExternalId(id).block();

        // Then
        assertNotNull(returnedCustomer);
        assertEquals(returnedCustomer.getExternalId(),id);
    }

    @Test
    void getAllOrders_AllOrdersReturned() {
        // Given
        String id = UUID.randomUUID().toString();
        Order secondOrder = order.withExternalId(id);
        List<Order> allOrders = new LinkedList<>();
        allOrders.add(order);
        allOrders.add(secondOrder);

        // When
        when(orderService.findAll()).thenReturn(Flux.fromIterable(allOrders));
        List<Order> returnedCustomers = orderFacade.getAllOrders().collectList().block();

        // Then
        assertNotNull(returnedCustomers);
        assertEquals(returnedCustomers.size(),2);
    }

}