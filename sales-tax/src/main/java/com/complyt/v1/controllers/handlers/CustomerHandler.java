package com.complyt.v1.controllers.handlers;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Customer", description = "This is the Customer controller")
public class CustomerHandler {

    @NonNull
    private final CustomerFacade customerfacade;

    @Operation(summary = "This will update the customer if found by externalId, otherwise it will create the customer")
    @CustomerUpdatePermission
    @ResponseStatus(HttpStatus.OK)
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");

        Mono<CustomerDto> customerDtoMono = serverRequest.bodyToMono(CustomerDto.class)
                .map(CustomerMapper.INSTANCE::customerDtoToCustomer).flatMap(receivedCustomer ->
                        customerfacade.findByExternalId(externalId)
                                .flatMap(originalCustomer -> customerfacade.updateIfModified(receivedCustomer, originalCustomer))
                                .switchIfEmpty(customerfacade.saveCustomer(receivedCustomer)))
                .map(CustomerMapper.INSTANCE::customerToCustomerDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @Operation(summary = "Gets customer by externalId")
    @CustomerReadPermission
    @ResponseStatus(code = HttpStatus.OK)
    public Mono<ServerResponse> getByExternalId(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");

        Mono<CustomerDto> customerDtoMono = customerfacade.findByExternalId(externalId)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundException("Customer not found")));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @Operation(summary = "Gets all matching customers by name")
    @CustomerReadPermission
    @GetMapping("name/{name}")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getByName(@NonNull @PathVariable("name") String name) {
        log.debug("Get customer by name - name received as path variable : " + name);

        return customerfacade.findByName(name)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .switchIfEmpty(Flux.error(new ObjectNotFoundException("No Customer with externalId " + name)));
    }

    @Operation(summary = "Gets all the customers")
    @CustomerReadPermission
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<CustomerDto> getAll() {
        return customerfacade.getAllCustomers().map(CustomerMapper.INSTANCE::customerToCustomerDto);
    }
}
