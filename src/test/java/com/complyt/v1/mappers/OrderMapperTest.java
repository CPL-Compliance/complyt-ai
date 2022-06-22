package com.complyt.v1.mappers;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.v1.model.OrderDto;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class OrderMapperTest {

    @Test
    void orderDtoToOrder() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        ObjectId clientId = new ObjectId();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000,4,8000,"description","name","taxCode",
                        null,new SalesTaxRate(0.5f,0.5f,0.5f,0.5f,0.5f,0.5f),false,0
                ));
            }
        };

        Order order = new Order(id, externalId, items, billingAddress, shippingAddress, customerId, null,null, OrderStatus.ACTIVE, clientId);
        OrderDto orderDto = OrderMapper.INSTANCE.orderToOrderDto(order);

    }

    @Test
    void orderToOrderDto() {
    }

    @Test
    void testOrderToOrderDto() {
    }
}