package com.complyt.service;

import com.complyt.model.Customer;
import com.complyt.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public List<Customer> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }
}
