package com.complyt.v1.controllers;


import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.CustomerDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Api("This is the Customer controller")
@AllArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customer";

    @NonNull
    private CustomerFacade customerfacade;

    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(value = "This will update the customer if found by externalId, otherwise it will create the customer", notes = "Some note")
    public Mono<CustomerDto> upsertCustomer(@RequestBody CustomerDto customerDto) {
        try {
            Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
            Mono<Customer> customerMono = customerfacade.upsert(customer);

            return customerMono.map(customerItem -> CustomerMapper.INSTANCE.customerToCustomerDto(customerItem));
        } catch (OperationFailedException operationFailedException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, customerDto.toString(), operationFailedException);
        }
    }

    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
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
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getCustomerByName(@RequestParam String name) {
        Flux<Customer> customers = customerfacade.findByName(name);

        return customers.map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getAllCustomers() {
        Flux<Customer> customers = customerfacade.getAllCustomers();

        return customers.map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item));
    }
}