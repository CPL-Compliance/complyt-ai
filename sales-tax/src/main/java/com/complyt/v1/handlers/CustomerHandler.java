package com.complyt.v1.handlers;

import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.RepositoryConstant;
import com.complyt.security.permissions.customer.CustomerReadPermission;
import com.complyt.security.permissions.customer.CustomerUpdatePermission;
import com.complyt.utils.observability.ContextLogger;
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
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        int offSet = Integer.parseInt(serverRequest.queryParam("offset")
                .orElse("0"));
        int limit = Integer.parseInt(serverRequest.queryParam("limit")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_SIZE)));

        Flux<CustomerDto> customerDtoFlux = ContextLogger.observeCtx(logStr, log::info).thenMany(customerfacade.getAll(offSet, limit)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .flatMapSequential(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getAllBySource(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<CustomerDto> customerDtoFlux = ContextLogger.observeCtx(logStr, log::info).thenMany(customerfacade.getAllBySource(source)
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto)));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }

    @CustomerUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        return ContextLogger.observeCtx(logStr, log::info).then(customerDtoValidationHandler.validate(serverRequest))
                .flatMap(customerDto -> ContextLogger.observeCtx(customerDto.toString(), log::info).thenReturn(customerDto))
                .map(CustomerMapper.INSTANCE::customerDtoToCustomer)
                .flatMap(receivedCustomer ->
                        customerfacade.findByExternalIdAndSource(externalId, source)
                                .flatMap(originalCustomer -> customerfacade.updateIfModified(receivedCustomer, originalCustomer)
                                        .flatMap(savedCustomer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(CustomerMapper.INSTANCE.customerToCustomerDto(savedCustomer))
                                                .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info)
                                                        .thenReturn(customerDto)), CustomerDto.class)))
                                .switchIfEmpty(customerfacade.saveCustomer(receivedCustomer).flatMap(savedCustomer -> ServerResponse.created(serverRequest.uri()).contentType(MediaType.APPLICATION_JSON).body(Mono.just(CustomerMapper.INSTANCE.customerToCustomerDto(savedCustomer))
                                        .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info)
                                                .thenReturn(customerDto)), CustomerDto.class))));
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByExternalIdAndSource(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<CustomerDto> customerDtoMono = ContextLogger.observeCtx(logStr, log::info).then(customerfacade.findByExternalIdAndSource(externalId, source))
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        //todo - when null complytId -> Send BadRequest 404 - null
        UUID complytId = UUID.fromString(serverRequest.pathVariable("complytId"));
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        Mono<CustomerDto> customerDtoMono = ContextLogger.observeCtx(logStr, log::info).then(customerfacade.findByComplytId(complytId))
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto))
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByName(ServerRequest serverRequest) {
        String name = serverRequest.pathVariable("name");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<CustomerDto> customerDtoFlux = ContextLogger.observeCtx(logStr, log::info).thenMany(customerfacade.findByName(name))
                .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }
}
