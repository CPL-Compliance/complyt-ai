package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.v1.model.OrderDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    @Test
    void orderDtoToOrder() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item("price", "quantity", "description", "name", "taxCode"));
            }
        };

        Order order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId,null, OrderStatus.ACTIVE);
        OrderDto orderDto = OrderMapper.INSTANCE.orderToOrderDto(order);

    }

    @Test
    void orderToOrderDto() {
    }

    @Test
    void testOrderToOrderDto() {
    }
}