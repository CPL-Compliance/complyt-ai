package com.complyt.services;

public interface ISalesTaxService {
    String getSalesTax(String zip, String address, String city, String state);
}
