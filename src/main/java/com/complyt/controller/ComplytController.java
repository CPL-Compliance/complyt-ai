package com.complyt.controller;

import com.complyt.entity.Customer;
import com.complyt.service.CustomersServices;
import com.complyt.service.SalesTaxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
//@RequestMapping("/v1")
public class ComplytController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    SalesTaxService salesTaxByAddressService;

    @Autowired
    CustomersServices customersServices;

    @GetMapping("/getSalesTax")
    public String getSalesTax(@RequestParam String zip, @RequestParam String address, @RequestParam String city,
                              @RequestParam String state) {
        return salesTaxByAddressService.getSalesTax(zip, address, city, state);
    }

    @PostMapping("/createCustomer")
    public Customer createCustomer(@RequestBody Customer customer){
        return customersServices.createCustomer(customer);
    }

    @GetMapping("/getCustomerByName")
    public Customer getCustomerByName(@RequestParam String name){
        return customersServices.getCustomerByName(name);
    }
}