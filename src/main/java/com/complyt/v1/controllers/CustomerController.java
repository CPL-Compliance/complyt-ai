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

import java.net.URI;

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
    public Mono<ResponseEntity<CustomerDto>> upsertCustomer(@PathVariable @NonNull String externalId
            , @RequestBody @NonNull CustomerDto customerDto) {

        Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);

        return customerfacade
                .upsert(customer)
                .map(item -> ResponseEntity.ok().body(CustomerMapper.INSTANCE.customerToCustomerDto(item)));
    }

    @Operation(summary = "Gets customer by externalId")
    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerDto>> getByExternalId(@NonNull @PathVariable("externalId") String externalId) {
        return customerfacade.findByExternalId(externalId)
                .map(customerItem -> ResponseEntity.ok().body(CustomerMapper.INSTANCE.customerToCustomerDto(customerItem)))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }

    @Operation(summary = "This will create a customer")
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<CustomerDto>> create(@RequestBody CustomerDto customerDto) {
        Customer customer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);

        return customerfacade.save(customer)
                .map(item -> ResponseEntity
                        .created(URI.create(BASE_URL + "/" + item.getExternalId()))
                        .body(CustomerMapper.INSTANCE.customerToCustomerDto(item)));
    }

    @Operation(summary = "Gets all matching customers by name")
    @GetMapping("name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getByName(@PathVariable("name") String name) {
        return customerfacade
                .findByName(name)
                .map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item))
                .switchIfEmpty(Flux.error(new NotFoundException(name)));
    }

    @Operation(summary = "Gets all the customers")
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getAll() {
        Flux<Customer> customers = customerfacade.getAllCustomers();

        return customers.map(item -> CustomerMapper.INSTANCE.customerToCustomerDto(item));
    }
}