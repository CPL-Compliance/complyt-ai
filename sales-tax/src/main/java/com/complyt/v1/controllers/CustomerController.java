package com.complyt.v1.controllers;


import com.complyt.facades.CustomerFacade;
import com.complyt.security.permissions.customer.CustomerCreatePermission;
import com.complyt.security.permissions.customer.CustomerReadPermission;
import com.complyt.security.permissions.customer.CustomerUpdatePermission;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.customer.CustomerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customers";

    @NonNull
    private final CustomerFacade customerfacade;

    @Operation(summary = "This will update the customer if found by externalId, otherwise it will create the customer")
    @CustomerUpdatePermission
    @PutMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerDto>> upsertCustomer(@PathVariable @NonNull String externalId,
                                                            @RequestBody @NonNull CustomerDto customerDto) {
        return customerfacade.upsert(CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto))
                .map(item -> ResponseEntity.ok().body(CustomerMapper.INSTANCE.customerToCustomerDto(item)));
    }

    @Operation(summary = "Gets customer by externalId")
    @CustomerReadPermission
    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerDto>> getByExternalId(@NonNull @PathVariable("externalId") String externalId) {
        log.debug("Get customer by external id - id received as path variable : " + externalId);

        return customerfacade.findByExternalId(externalId)
                .map(customerItem -> ResponseEntity.ok().body(CustomerMapper.INSTANCE.customerToCustomerDto(customerItem)))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build())).log();
    }

    @Operation(summary = "This will create a customer")
    @CustomerCreatePermission
    @PostMapping("")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<CustomerDto>> create(@NonNull @RequestBody CustomerDto customerDto) {
        log.debug("Create customer - DTO received in request body : " + customerDto);

        return customerfacade.save(CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto))
                .map(customer -> ResponseEntity.created(URI.create(BASE_URL + "/" + customer.getExternalId()))
                        .body(CustomerMapper.INSTANCE.customerToCustomerDto(customer)));
    }

    @Operation(summary = "Gets all matching customers by name")
    @CustomerReadPermission
    @GetMapping("name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getByName(@NonNull @PathVariable("name") String name) {
        log.debug("Get customer by name - name received as path variable : " + name);

        return customerfacade.findByName(name)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .switchIfEmpty(Flux.error(new NotFoundException(name))).log();
    }

    @Operation(summary = "Gets all the customers")
    @CustomerReadPermission
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getAll() {
        return customerfacade.getAllCustomers().map(CustomerMapper.INSTANCE::customerToCustomerDto);
    }
}