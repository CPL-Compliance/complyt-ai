package com.complyt.v1.handlers;

import com.complyt.facades.CustomerFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.permissions.customer.CustomerReadPermission;
import com.complyt.security.permissions.customer.CustomerUpdatePermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.routers.CustomerRouter;
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

import java.net.URI;
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

        String page = serverRequest.queryParam("page")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_NUM));
        String size = serverRequest.queryParam("size")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_SIZE));

        Flux<CustomerDto> customerDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(customerDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> customerfacade.getAll(Integer.parseInt(page), Integer.parseInt(size))
                        .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                        .flatMapSequential(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto))));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getAllBySource(ServerRequest serverRequest) {
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Flux<CustomerDto> customerDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(customerDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> customerfacade.getAllBySource(source)
                        .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                        .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto))));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoFlux, CustomerDto.class);
    }

    @CustomerUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        String resourceURI = CustomerRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId;

        return ContextLogger.observeCtx(logStr, log::info)
                .then(customerDtoValidationHandler.handle(serverRequest))
                .flatMap(customerDto -> ContextLogger.observeCtx(customerDto.toString(), log::info).thenReturn(customerDto))
                .map(CustomerMapper.INSTANCE::customerDtoToCustomer)
                .flatMap(receivedCustomer ->
                        customerfacade.findByExternalIdAndSource(externalId, source)
                                .flatMap(originalCustomer -> customerfacade.updateIfModified(receivedCustomer, originalCustomer)
                                        .flatMap(savedCustomer -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(CustomerMapper.INSTANCE.customerToCustomerDto(savedCustomer))
                                                .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info)
                                                        .thenReturn(customerDto)), CustomerDto.class)))
                                .switchIfEmpty(customerfacade.saveCustomer(receivedCustomer).flatMap(savedCustomer ->
                                        ServerResponse.created(URI.create(resourceURI)).contentType(MediaType.APPLICATION_JSON).body(Mono.just(CustomerMapper.INSTANCE.customerToCustomerDto(savedCustomer))
                                                .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info)
                                                        .thenReturn(customerDto)), CustomerDto.class))));
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        String complytId = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<CustomerDto> customerDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(customerDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> customerfacade.findByComplytId(UUID.fromString(complytId))
                        .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                        .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info)
                                .thenReturn(customerDto))
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()))));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }

    @CustomerReadPermission
    public Mono<ServerResponse> getByExternalIdAndSource(ServerRequest serverRequest) {
        String externalId = serverRequest.pathVariable("externalId");
        String source = serverRequest.pathVariable("source");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<CustomerDto> customerDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(customerDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> customerfacade.findByExternalIdAndSource(externalId, source))
                        .map(CustomerMapper.INSTANCE::customerToCustomerDto)
                        .flatMap(customerDto -> ContextLogger.observeCtx("<-- Returned Body: " + customerDto, log::info).thenReturn(customerDto))
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(customerDtoMono, CustomerDto.class);
    }
}
