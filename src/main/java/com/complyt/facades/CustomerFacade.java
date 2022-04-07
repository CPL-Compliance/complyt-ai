package com.complyt.facades;

import com.complyt.domain.Customer;
import com.complyt.services.CustomerService;
import com.mongodb.client.result.UpdateResult;
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

    public Customer save(Customer customer) {
        return customerService.save(customer);
    }

    public UpdateResult update(Customer customer) {
        return customerService.update(customer);
    }

    public List<Customer> findByName(String name) {
        return customerService.findByName(name);
    }

    public List<Customer> getAllCustomers() {
        return customerService.findAll();
    }
}
