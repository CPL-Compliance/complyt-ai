package com.complyt.services;

import com.complyt.domain.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);

    List<Customer> findByName(String name);

    List<Customer> findAll();

    Customer save(Customer customer);
}