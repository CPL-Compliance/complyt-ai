package com.complyt.v1.handlers;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.repositories.RepositoryConstant;
import com.complyt.security.permissions.sales_tax_tracking.NexusReadPermission;
import com.complyt.security.permissions.sales_tax_tracking.NexusUpdatePermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.DateWrapperToLocalDateMapper;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.SalesTaxTrackingDto;
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

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info).then(salesTaxTrackingFacade.findByState(state))
                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);

    }

    @NexusReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest serverRequest) {
        UUID complytId = UUID.fromString(serverRequest.pathVariable("complytId"));
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info).then(salesTaxTrackingFacade.findByComplytId(complytId))
                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);
    }

    @NexusUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest serverRequest) {
        String state = serverRequest.pathVariable("state");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        return ContextLogger.observeCtx(logStr, log::info).then(salesTaxTrackingDtoValidationHandler.validate(serverRequest))
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
        int page = Integer.parseInt(serverRequest.queryParam("page")
                .orElse("0"));
        int size = Integer.parseInt(serverRequest.queryParam("size")
                .orElse(String.valueOf(RepositoryConstant.DEFAULT_PAGE_SIZE)));

        return ContextLogger.observeCtx(logStr, log::info).then(
                ServerResponse.ok()
                        .body(salesTaxTrackingFacade.findAll(page, size).map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto), SalesTaxTrackingDto.class));
    }

    @NexusUpdatePermission
    public Mono<ServerResponse> refreshNexusSummaryByDate(ServerRequest serverRequest) {
        String state = serverRequest.pathVariable("state");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());


        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info).then(
                dateWrapperDtoValidationHandler.validate(serverRequest)
                        .map(DateWrapperToLocalDateMapper.INSTANCE::dateWrapperToLocalDate)
                        .flatMap(date -> salesTaxTrackingFacade.refreshNexusSummary(state, date)
                                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                                .switchIfEmpty(Mono.error(ObjectNotFoundApiException::new))));

        return ServerResponse.ok().body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);
    }
}