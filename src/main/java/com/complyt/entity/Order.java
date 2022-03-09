package com.complyt.entity;

import java.util.Date;

public class Order {
    private String externalId;
    private Date creationDate;
    private String type;
    private Address toAddress;
    private String customer;
    private int price;

    public Date getCreationDate() {
        return creationDate;
    }

    public String getCustomer() {
        return customer;
    }

    public String getType() {
        return type;
    }

    public int getPrice() {
        return price;
    }

    public Address getToAddress() {
        return toAddress;
    }

    public Order(Date creationDate, String type, Address toAddress, String customer, int price) {
        this.creationDate = creationDate;
        this.type = type;
        this.toAddress = toAddress;
        this.customer = customer;
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "externalId='" + externalId + '\'' +
                ", creationDate=" + creationDate +
                ", type='" + type + '\'' +
                ", toAddress=" + toAddress +
                ", customer='" + customer + '\'' +
                ", price=" + price +
                '}';
    }
}
