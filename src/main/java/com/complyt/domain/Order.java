package com.complyt.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "order")
public class Order {
    @Id
    private String id;

    @DBRef
    private Customer customer;
    private String type;
    private int units;
    private int price;

    public String getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getType() {
        return type;
    }

    public int getUnits() {
        return units;
    }

    public int getPrice() {
        return price;
    }

    public Order(Customer customer, String type, int units, int price) {
        this.customer = customer;
        this.type = type;
        this.units = units;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + id + '\'' +
                ", customer='" + customer + '\'' +
                ", type='" + type + '\'' +
                ", units=" + units +
                ", price=" + price +
                '}';
    }
}
