package com.complyt.v1.controllers;


import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.services.exceptions.ResourceNotFoundException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.CustomerDto;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customer";

    private CustomerFacade customerfacade;

    @PostMapping("")
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        try {
            Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
            Customer createdCustomer = customerfacade.save(customer);

            return CustomerMapper.INSTANCE.customerToCustomerDto(createdCustomer);
        } catch (ResourceNotFoundException exc) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, customerDto.toString(), exc);
        }
    }

    @PutMapping("")
    public CustomerDto upsertCustomer(@RequestBody CustomerDto customerDto) {
        try {
            Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
            Customer createdCustomer = customerfacade.save(customer);

            return CustomerMapper.INSTANCE.customerToCustomerDto(createdCustomer);
        } catch (OperationFailedException operationFailedException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, customerDto.toString(), operationFailedException);
        }
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
