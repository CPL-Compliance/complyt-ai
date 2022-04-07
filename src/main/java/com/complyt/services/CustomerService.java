package com.complyt.services;

import com.complyt.domain.Customer;
import com.mongodb.client.result.UpdateResult;

import java.util.List;

public interface CustomerService extends CrudService<Customer, String> {
    Customer save(Customer customer);

    UpdateResult update(Customer customer);

    Customer findOneByName(String name);

    List<Customer> findAll();
}