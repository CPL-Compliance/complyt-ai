package com.complyt.v1.controllers;


import com.complyt.domain.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.CustomerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Tag(name = "Customer", description = "This is the Customer controller")
@AllArgsConstructor
@RestController
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customers";

    @NonNull
    private final CustomerFacade customerfacade;

    @Operation(summary = "This will update the customer if found by externalId, otherwise it will create the customer")
    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Mono<CustomerDto> upsertCustomer(@RequestBody CustomerDto customerDto) {
        Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Mono<Customer> customerMono = customerfacade.upsert(customer);

        return customerMono.map(customerItem -> CustomerMapper.INSTANCE.customerToCustomerDto(customerItem));
    }

    @Operation(summary = "Gets customer by externalId")
    @GetMapping("findByExternalId")
    public Mono<ResponseEntity<CustomerDto>> getByExternalId(@RequestParam String externalId) {
        return customerfacade.findByExternalId(externalId)
                .map(customerItem -> new ResponseEntity<>(CustomerMapper.INSTANCE.customerToCustomerDto(customerItem), HttpStatus.OK))
                .switchIfEmpty(Mono.error(new NotFoundException(externalId)));
    }

    @Operation(summary = "This will create a customer")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDto create(@RequestBody CustomerDto customerDto) {
        Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
        Customer createdCustomer = customerfacade.save(customer);

        return CustomerMapper.INSTANCE.customerToCustomerDto(createdCustomer);
    }

    @Operation(summary = "Gets all matching customers by name")
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getByName(@RequestParam String name) {
        return customerfacade
                .findByName(name)
                .map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item))
                .switchIfEmpty(Flux.error(new NotFoundException(name)));
    }

    @Operation(summary = "Gets all the customers")
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getAll() {
        Flux<Customer> customers = customerfacade.getAllCustomers();

        return customers.map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item));
    }
}