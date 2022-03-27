package com.complyt.services;

public interface SalesTaxService {
    String findByAddress(String zip, String address, String city, String state);
}
