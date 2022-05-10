package com.complyt.domain;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.CustomerDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemTest {

    private Item item;
    private Item anotherItem;

    @BeforeEach
    void setUp() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        AddressDto address = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        item = new Item(2000,4,8000,"description","name","taxCode");
        anotherItem = item.withName("anotherName");
    }

    @Test
    void equals_IdenticalCustomers_Equal() {
        anotherItem = anotherItem.withName(item.getName());
        assertEquals(item, anotherItem);
    }

    @Test
    void hashCode_IdenticalCustomers_Equal() {
        anotherItem = anotherItem.withName(item.getName());
        assertEquals(item.hashCode(), anotherItem.hashCode());
    }


    @Test
    void testEquals() {
    }

    @Test
    void canEqual() {
    }

    @Test
    void testHashCode() {
    }

    @Test
    void getUnitPrice() {
    }

    @Test
    void getQuantity() {
    }

    @Test
    void getTotalPrice() {
    }

    @Test
    void getDescription() {
    }

    @Test
    void getName() {
    }

    @Test
    void getTaxCode() {
    }

    @Test
    void testToString() {
    }
}