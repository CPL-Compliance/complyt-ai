package com.complyt.v1.controllers;


import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.CustomerDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customer";

    @NonNull
    private CustomerFacade customerfacade;

    @PostMapping("")
    public Mono<CustomerDto> createCustomer(@RequestBody CustomerDto customerDto) {
        Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Mono<Customer> customerMono = customerfacade.createCustomer(customer);

        return customerMono.map(customerItem -> CustomerMapper.INSTANCE.customerToCustomerDto(customerItem));
    }

    @GetMapping("")
    public Flux<CustomerDto> getCustomerByName(@RequestParam String name) {
        Flux<Customer> customers = customerfacade.findByName(name);

        return customers.map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item));
    }

    @GetMapping("/all")
    public Flux<CustomerDto> getAllCustomers() {
        Flux<Customer> customers = customerfacade.getAllCustomers();

        return customers.map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item));
    }
}