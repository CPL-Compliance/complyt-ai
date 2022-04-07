package com.complyt.services;

import com.complyt.domain.Customer;
import com.mongodb.client.result.UpdateResult;
import lombok.NonNull;

import java.util.List;

public interface CustomerService extends CrudService<Customer, String> {
    Customer save(@NonNull Customer customer);

    public Customer upsert(@NonNull Customer customer);

    Customer findOneByName(String name);

    List<Customer> findAll();
}