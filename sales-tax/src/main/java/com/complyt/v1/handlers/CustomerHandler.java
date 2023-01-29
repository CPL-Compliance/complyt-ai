package com.complyt.v1.handlers;

import com.complyt.facades.CustomerFacade;
import com.complyt.security.permissions.customer.CustomerReadPermission;
import com.complyt.security.permissions.customer.CustomerUpdatePermission;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CustomerHandler {

    @NonNull
    CustomerFacade customerfacade;

    @NonNull
    ValidationHandler<CustomerDto, SpringValidatorAdapter> customerDtoValidationHandler;

    @CustomerReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        Flux<CustomerDto> customerDtoFlux = customerfacade.getAll()
                .map(CustomerMapper.INSTANCE::customerToCustomerDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getAllBySource(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");
        Flux<CustomerDto> customerDtoFlux = customerfacade.getAllBySource(source)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto);

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }

    @CustomerUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");

        return customerDtoValidationHandler.validate(serverRequest)
                .map(customerDto ->
                        CustomerMapper.INSTANCE.customerDtoToCustomer(customerDto))
                .flatMap(receivedCustomer ->
                        customerfacade.findByExternalIdAndSource(externalId, source)
                                .flatMap(originalCustomer -> customerfacade.updateIfModified(receivedCustomer, originalCustomer)
                                        .flatMap(savedCustomer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(CustomerMapper.INSTANCE.customerToCustomerDto(savedCustomer)), CustomerDto.class)))
                                .switchIfEmpty(customerfacade.saveCustomer(receivedCustomer).flatMap(savedCustomer -> ServerResponse.created(serverRequest.uri()).contentType(MediaType.APPLICATION_JSON).body(Mono.just(CustomerMapper.INSTANCE.customerToCustomerDto(savedCustomer)), CustomerDto.class))));
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByExternalIdAndSource(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");

        Mono<CustomerDto> customerDtoMono = customerfacade.findByExternalIdAndSource(externalId, source)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        UUID complytId = UUID.fromString(serverRequest.pathVariable("complytId"));

        Mono<CustomerDto> customerDtoMono = customerfacade.findByComplytId(complytId)
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
