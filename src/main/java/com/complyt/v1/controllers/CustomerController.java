package com.complyt.v1.controllers;

import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_URL)
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CustomerController {
    public static final String BASE_URL = "/v1/customer";

    private CustomerFacade customerfacade;

    @PostMapping("")
    public Customer createCustomer(@RequestBody Customer customer){
        return customerfacade.createCustomer(customer);
    }

    @GetMapping("")
    public List<Customer> getCustomerByName(@RequestParam String name){
        return customerfacade.getCustomerByName(name);
    }

    @GetMapping("/all")
    public List<Customer> getAllCustomers(){
        return customerfacade.getAllCustomers();
    }
}
