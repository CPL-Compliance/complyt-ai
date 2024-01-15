package com.complyt.v1.handlers;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.permissions.sales_tax_tracking.NexusReadPermission;
import com.complyt.security.permissions.sales_tax_tracking.NexusUpdatePermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.DateWrapperToLocalDateMapper;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.exemption.ExemptionWrapperDto;
import com.complyt.v1.models.nexus.DateWrapperDto;
import com.complyt.v1.validators.ValidationHandler;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
public class SalesTaxTrackingHandler {

    @NonNull
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    @NonNull
    ValidationHandler<SalesTaxTrackingDto, SpringValidatorAdapter> salesTaxTrackingDtoValidationHandler;

    @NonNull
    ValidationHandler<DateWrapperDto, SpringValidatorAdapter> dateWrapperDtoValidationHandler;

    @NexusReadPermission
    public Mono<ServerResponse> getOne(ServerRequest serverRequest) {
        String state = serverRequest.pathVariable("state");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(salesTaxTrackingDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> salesTaxTrackingFacade.findByState(state))
                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);

    }

    @NexusReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        String complytId = serverRequest.pathVariable("complytId");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(salesTaxTrackingDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Mono.defer(() -> salesTaxTrackingFacade.findByComplytId(UUID.fromString(complytId)))
                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);
    }

    @NexusUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String state = serverRequest.pathVariable("state");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        return ContextLogger.observeCtx(logStr, log::info)
                .then(salesTaxTrackingDtoValidationHandler.handle(serverRequest))
                .flatMap(salesTaxTrackingDto -> {
                    SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto);
                    return salesTaxTrackingFacade.findByState(state)
                            .flatMap(originalSalesTaxTracking -> salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking))
                            .flatMap(updatedSalesTaxTracking ->
                                    ServerResponse.status(HttpStatus.OK).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(updatedSalesTaxTracking)))
                            .switchIfEmpty(salesTaxTrackingFacade.save(receivedSalesTaxTracking)
                                    .flatMap(salesTaxTracking ->
                                            ServerResponse.status(HttpStatus.CREATED).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking))));
                });
    }


    @NexusReadPermission
    public Mono<ServerResponse> getAll(ServerRequest serverRequest) {
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());
        String page = serverRequest.queryParam("page")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_NUM));
        String size = serverRequest.queryParam("size")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_SIZE));

        Flux<SalesTaxTrackingDto> salesTaxTrackingDtoFlux = ContextLogger.observeCtx(logStr, log::info)
                .thenMany(salesTaxTrackingDtoValidationHandler.handle(serverRequest))
                .switchIfEmpty(Flux.defer(() -> salesTaxTrackingFacade.findAll(Integer.parseInt(page), Integer.parseInt(size))
                                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)));

        return ServerResponse.ok().body(salesTaxTrackingDtoFlux, SalesTaxTrackingDto.class);

    }

    @NexusUpdatePermission
    public Mono<ServerResponse> refreshNexusSummaryByDate(ServerRequest serverRequest) {
        String state = serverRequest.pathVariable("state");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(dateWrapperDtoValidationHandler.handle(serverRequest)
                        .map(DateWrapperToLocalDateMapper.INSTANCE::dateWrapperToLocalDate)
                        .flatMap(date -> salesTaxTrackingFacade.refreshNexusSummary(state, date)
                                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                                .switchIfEmpty(Mono.error(ObjectNotFoundApiException::new))));

        return ServerResponse.ok().body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);
    }
}