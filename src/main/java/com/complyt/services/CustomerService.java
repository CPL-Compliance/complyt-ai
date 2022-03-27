package com.complyt.services;

import com.complyt.domain.Customer;

import java.util.List;

public interface CustomerService {
    Customer createCustomer(Customer customer);

    List<Customer> getCustomerByName(String name);

    List<Customer> getAllCustomers();

    Customer save(Customer customer);
}