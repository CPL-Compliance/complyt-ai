package com.complyt.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "client")
public class Client {
    @Id
    private String id;
    private String name;
    private Address address;

    @DBRef
    private List<Order> orders;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Address getAddress() {
        return address;
    }

    public Client(String name, Address address) {
        this.name = name;
        this.address = address;
    }

    public List<Order> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", orders=" + orders +
                '}';
    }
}
