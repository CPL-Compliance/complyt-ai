package com.complyt.v1.controllers;


import com.complyt.domain.customer.Customer;
import com.complyt.facades.CustomerFacade;
import com.complyt.security.permissions.customer.CustomerReadPermission;
import com.complyt.security.permissions.customer.CustomerUpdatePermission;
import com.complyt.v1.exceptions.ObjectNotFoundException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.customer.CustomerDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer", description = "This is the Customer controller")
@AllArgsConstructor
@RestController
@Slf4j
@RequestMapping(CustomerController.BASE_URL)
public class CustomerController {
    public static final String BASE_URL = "/v1/customers";

    @NonNull
    private final CustomerFacade customerfacade;

    @Operation(summary = "This will update the customer if found by externalId and source, otherwise it will create the customer")
    @CustomerUpdatePermission
    @PutMapping("source/{source}/externalId/{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerDto>> upsert(@PathVariable("externalId") @NonNull String externalId, @PathVariable("source") @NonNull String source,
                                                    @RequestBody @NonNull CustomerDto customerDto) {
        log.debug("Upsert customer - DTO received in request body : " + customerDto);
        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);

        return customerfacade.findByExternalId(externalId, source)
                .flatMap(originalCustomer -> customerfacade.updateIfModified(receivedCustomer, originalCustomer))
                .map(updatedCustomer -> ResponseEntity.status(HttpStatus.OK).body(CustomerMapper.INSTANCE.customerToCustomerDto(updatedCustomer)))
                .switchIfEmpty(customerfacade.saveCustomer(receivedCustomer)
                        .map(customer -> ResponseEntity.status(HttpStatus.CREATED).body(CustomerMapper.INSTANCE.customerToCustomerDto(customer))));

    }

    @Operation(summary = "Gets customer by externalId and source")
    @CustomerReadPermission
    @GetMapping("source/{source}/externalId/{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerDto>> getByExternalId(@NonNull @PathVariable("externalId") String externalId, @PathVariable("source") @NonNull String source) {
        log.debug("Get customer by external id and source - id and source received as path variables : " + externalId + ", " + source);

        return customerfacade.findByExternalId(externalId, source)
                .map(customerItem -> ResponseEntity.ok().body(CustomerMapper.INSTANCE.customerToCustomerDto(customerItem)))
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("No Customer with externalId " + externalId + ", in source " + source)));
    }

    @Operation(summary = "Gets customer by complytId")
    @CustomerReadPermission
    @GetMapping("complytId/{complytId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<CustomerDto>> getByComplytId(@PathVariable("complytId") @NonNull UUID complytId) {
        log.debug("Get customer by complyt id - id received as path variable : " + complytId);

        return customerfacade.findByComplytId(complytId)
                .map(customerItem -> ResponseEntity.ok().body(CustomerMapper.INSTANCE.customerToCustomerDto(customerItem)))
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("No Customer with complytId " + complytId)));
    }

    @Operation(summary = "Gets all matching customers by name")
    @CustomerReadPermission
    @GetMapping("name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getByName(@NonNull @PathVariable("name") String name) {
        log.debug("Get customer by name - name received as path variable : " + name);

        return customerfacade.findByName(name)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .switchIfEmpty(Flux.error(new ObjectNotFoundException("No Customer with name " + name)));
    }

    @Operation(summary = "Gets all the customers")
    @CustomerReadPermission
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getAll() {
        return customerfacade.getAll().map(CustomerMapper.INSTANCE::customerToCustomerDto);
    }

    @Operation(summary = "Gets all the customers in source ")
    @CustomerReadPermission
    @GetMapping("source/{source}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getAllBySource(@PathVariable("source") @NonNull String source) {
        return customerfacade.getAllBySource(source).map(CustomerMapper.INSTANCE::customerToCustomerDto);
    }
}