package com.complyt.v1.controllers;


import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.CustomerDto;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customer";

    private CustomerFacade customerfacade;

    @PostMapping("")
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);

        Customer createdCustomer = customerfacade.createCustomer(customer);

        return CustomerMapper.INSTANCE.customerToCustomerDto(createdCustomer);
    }

    @GetMapping("")
    public List<CustomerDto> getCustomerByName(@RequestParam String name) {
        List<Customer> customers = customerfacade.findByName(name);

        return CustomerMapper.INSTANCE.customersToCustomerDtos(customers);
    }

    @GetMapping("/all")
    public List<CustomerDto> getAllCustomers() {
        List<Customer> customers = customerfacade.getAllCustomers();

        return CustomerMapper.INSTANCE.customersToCustomerDtos(customers);
    }
}
