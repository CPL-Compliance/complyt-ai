package com.complyt.facades;

import com.complyt.domain.Customer;
import com.complyt.services.CustomerService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class CustomerFacade {

    @Qualifier("customerServiceImpl")
    @NonNull
    private CustomerService customerService;

    public Customer createCustomer(Customer customer) {
        return customerService.createCustomer(customer);
    }

    public List<Customer> getCustomerByName(String name) {
        return customerService.findByName(name);
    }

    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }
}
