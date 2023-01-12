package com.complyt.v1.handlers;

import com.complyt.facades.CustomerFacade;
import com.complyt.security.permissions.customer.CustomerReadPermission;
import com.complyt.security.permissions.customer.CustomerUpdatePermission;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.model.customer.CustomerDto;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerHandler {

    @NonNull
    CustomerFacade customerfacade;

    @CustomerReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Flux<CustomerDto> customerDtoMono = customerfacade.getAllCustomers()
                .map(CustomerMapper.INSTANCE::customerToCustomerDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @CustomerUpdatePermission
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

    @CustomerReadPermission
    public Mono<ServerResponse> getByExternalId(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");

        Mono<CustomerDto> customerDtoMono = customerfacade.findByExternalId(externalId)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByName(ServerRequest serverRequest) {
        String name = serverRequest.pathVariable("name");

        Flux<CustomerDto> customerDtoFlux = customerfacade.findByName(name)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }
}
