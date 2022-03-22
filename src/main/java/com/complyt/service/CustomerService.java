package com.complyt.service;

import com.complyt.domain.Customer;
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
        Customer customerResult = customerRepository.save(customer);

        return customerResult;
    }

    public List<Customer> getCustomerByName(String name) {
        return customerRepository.findByName(name);
    }

    public List<Customer> getAllCustomers() {
        List<Customer> customers = customerRepository.getAllCustomers();

        return customers;
    }

    public Customer save(Customer customer) {
        if (customer == null) {
            return null;
        }

        return customerRepository.save(customer);
    }
}