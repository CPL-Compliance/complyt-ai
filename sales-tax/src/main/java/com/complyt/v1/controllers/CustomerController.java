//package com.complyt.v1.controllers;
//
//
//import com.complyt.domain.customer.Customer;
//import com.complyt.facades.CustomerFacade;
//import com.complyt.security.permissions.customer.CustomerReadPermission;
//import com.complyt.security.permissions.customer.CustomerUpdatePermission;
//import com.complyt.v1.exceptions.ObjectNotFoundException;
//import com.complyt.v1.mappers.CustomerMapper;
//import com.complyt.v1.model.customer.CustomerDto;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.security.SecurityRequirement;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import lombok.AllArgsConstructor;
//import lombok.NonNull;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.webjars.NotFoundException;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//@SecurityRequirement(name = "bearerAuth")
//@Tag(name = "Customer", description = "This is the Customer controller")
//@AllArgsConstructor
//@RestController
//@Slf4j
//@RequestMapping(CustomerController.BASE_URL)
//public class CustomerController {
//    public static final String BASE_URL = "/v1/customers";
//
//    @NonNull
//    private final CustomerFacade customerfacade;
//
//    @Operation(summary = "This will update the customer if found by externalId, otherwise it will create the customer")
//    @CustomerUpdatePermission
//    @PutMapping("{externalId}")
//    @ResponseStatus(HttpStatus.OK)
//    public Mono<ResponseEntity<CustomerDto>> upsert(@PathVariable @NonNull String externalId,
//                                                    @RequestBody @NonNull CustomerDto customerDto) {
//        log.debug("Upsert customer - DTO received in request body : " + customerDto);
//        Customer receivedCustomer = CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto);
//
//        return customerfacade.findByExternalId(externalId)
//                .flatMap(originalCustomer -> customerfacade.updateIfModified(receivedCustomer, originalCustomer))
//                .map(updatedCustomer -> ResponseEntity.status(HttpStatus.OK).body(CustomerMapper.INSTANCE.customerToCustomerDto(updatedCustomer)))
//                .switchIfEmpty(customerfacade.saveCustomer(receivedCustomer)
//                        .map(customer -> ResponseEntity.status(HttpStatus.CREATED).body(CustomerMapper.INSTANCE.customerToCustomerDto(customer))));
//
//    }
//
//    @Operation(summary = "Gets customer by externalId")
//    @CustomerReadPermission
//    @GetMapping("{externalId}")
//    @ResponseStatus(HttpStatus.OK)
//    public Mono<ResponseEntity<CustomerDto>> getByExternalId(@NonNull @PathVariable("externalId") String externalId) {
//        log.debug("Get customer by external id - id received as path variable : " + externalId);
//
//        return customerfacade.findByExternalId(externalId)
//                .map(customerItem -> ResponseEntity.ok().body(CustomerMapper.INSTANCE.customerToCustomerDto(customerItem)))
//                .switchIfEmpty(Mono.error(new ObjectNotFoundException("No Customer with externalId " + externalId)));
//    }
//
//    @Operation(summary = "Gets all matching customers by name")
//    @CustomerReadPermission
//    @GetMapping("name/{name}")
//    @ResponseStatus(HttpStatus.OK)
//    public Flux<CustomerDto> getByName(@NonNull @PathVariable("name") String name) {
//        log.debug("Get customer by name - name received as path variable : " + name);
//
//        return customerfacade.findByName(name)
//                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
//                .switchIfEmpty(Flux.error(new ObjectNotFoundException("No Customer with externalId " + name)));
//    }
//
//    @Operation(summary = "Gets all the customers")
//    @CustomerReadPermission
//    @GetMapping("")
//    @ResponseStatus(HttpStatus.OK)
//    public Flux<CustomerDto> getAll() {
//        return customerfacade.getAllCustomers().map(CustomerMapper.INSTANCE::customerToCustomerDto);
//    }
//}