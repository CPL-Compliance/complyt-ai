package com.complyt.v1.controllers;


import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.CustomerDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customer";

    @NonNull
    private CustomerFacade customerfacade;

    @PutMapping("")
    public Mono<CustomerDto> upsertCustomer(@RequestBody CustomerDto customerDto) {
        try {
            Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
            Mono<Customer> customerMono = customerfacade.upsert(customer);

            return customerMono.map(customerItem -> CustomerMapper.INSTANCE.customerToCustomerDto(customerItem));
        } catch (OperationFailedException operationFailedException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, customerDto.toString(), operationFailedException);
        }
    }

    @GetMapping("")
    public Mono<CustomerDto> getCustomerByExternalId(@RequestParam String externalId) {
        try {
            Mono<Customer> customerMono = customerfacade.findByfindByExternalId(externalId);

            return customerMono.map(customerItem -> CustomerMapper.INSTANCE.customerToCustomerDto(customerItem));
        } catch (OperationFailedException operationFailedException) {
            String reason = String.format("Customer with External id of %s cannot be found",externalId);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("")
    public CustomerDto createCustomer(@RequestBody CustomerDto customerDto) {
        try {
            Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
            Customer createdCustomer = customerfacade.save(customer);

            return CustomerMapper.INSTANCE.customerToCustomerDto(createdCustomer);
        } catch (OperationFailedException operationFailedException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, customerDto.toString(), operationFailedException);
        }
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