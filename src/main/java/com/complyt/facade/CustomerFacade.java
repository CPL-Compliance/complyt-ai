package com.complyt.facade;

import com.complyt.domain.Customer;
import com.complyt.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomerFacade {

    @Autowired
    CustomerService customerService;

    public Customer createCustomer(Customer customer) {
        return customerService.createCustomer(customer);
    }

    public List<Customer> getCustomerByName(String name) {
        return customerService.getCustomerByName(name);
    }

    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }
}
