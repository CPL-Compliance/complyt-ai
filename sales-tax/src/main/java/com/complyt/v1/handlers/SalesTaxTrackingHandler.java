package com.complyt.v1.handlers;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.repositories.Constants.RepositoryConstant;
import com.complyt.security.permissions.sales_tax_tracking.NexusReadPermission;
import com.complyt.security.permissions.sales_tax_tracking.NexusUpdatePermission;
import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.exceptions.types.InvalidPatchFieldException;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.DateWrapperToLocalDateMapper;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.nexus.DateWrapperDto;
import com.complyt.v1.routers.SalesTaxTrackingRouter;
import com.complyt.v1.validators.Patcher;
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

import java.net.URI;
import java.util.Map;
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

    @NonNull
    Patcher<SalesTaxTrackingDto> salesTaxTrackingPatcher;

    @NexusReadPermission
    public Mono<ServerResponse> getOne(ServerRequest serverRequest) {
        String country = serverRequest.queryParam("country").orElse("");
        String state = serverRequest.queryParam("state").orElse("");
        String subsidiary = serverRequest.queryParam("subsidiary").orElse("0");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(salesTaxTrackingDtoValidationHandler.validateQueryParam("country", country))
                .then(CountryIsUsaChecker.isCountryUsa(country) ? salesTaxTrackingDtoValidationHandler.validateQueryParam("state", state) : Mono.just(""))
                .then(Mono.defer(() -> salesTaxTrackingFacade.findByCountryAndState(country, state, subsidiary)
                        .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException()))));

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
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        return ContextLogger.observeCtx(logStr, log::info)
                .then(salesTaxTrackingDtoValidationHandler.handle(serverRequest))
                .flatMap(salesTaxTrackingDto -> {

                    String country = salesTaxTrackingDto.country();
                    String stateName = null;
                    String subsidiary = salesTaxTrackingDto.subsidiaryDto().subsidiaryId();
                    StringBuilder resourceURI = new StringBuilder(SalesTaxTrackingRouter.BASE_URL + "?country=" + country);

                    if (salesTaxTrackingDto.state() != null) {
                        resourceURI.append("&state=" + salesTaxTrackingDto.state().name());
                        stateName = salesTaxTrackingDto.state().name();
                    }

                    SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto);
                    return salesTaxTrackingFacade.findByCountryAndState(country, stateName, subsidiary)
                            .flatMap(originalSalesTaxTracking -> salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking))
                            .flatMap(updatedSalesTaxTracking ->
                                    ServerResponse.status(HttpStatus.OK).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(updatedSalesTaxTracking)))
                            .switchIfEmpty(salesTaxTrackingFacade.save(receivedSalesTaxTracking)
                                    .flatMap(salesTaxTracking ->
                                            ServerResponse.created(URI.create(resourceURI.toString())).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking))));
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
        String country = serverRequest.queryParam("country").orElse("");
        String state = serverRequest.queryParam("state").orElse("");
        String subsidiary = serverRequest.queryParam("subsidiary").orElse("0");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(dateWrapperDtoValidationHandler.handle(serverRequest)
                        .map(DateWrapperToLocalDateMapper.INSTANCE::dateWrapperToLocalDate)
                        .flatMap(date -> salesTaxTrackingDtoValidationHandler.validateQueryParam("country", country)
                                .then(CountryIsUsaChecker.isCountryUsa(country) ? salesTaxTrackingDtoValidationHandler.validateQueryParam("state", state) : Mono.empty())
                                .then(Mono.defer(() -> salesTaxTrackingFacade.refreshNexusSummary(country, state, date, subsidiary)
                                        .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                                        .switchIfEmpty(Mono.error(ObjectNotFoundApiException::new))))));

        return ServerResponse.ok().body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);
    }

    @NexusUpdatePermission
    public Mono<ServerResponse> patch(ServerRequest serverRequest) {
        String country = serverRequest.queryParam("country").orElse("");
        String state = serverRequest.queryParam("state").orElse("");
        String subsidiary = serverRequest.queryParam("subsidiary").orElse("0");
        String logStr = String.format("--> Request Received; Method -> %s, Path -> %s", serverRequest.method(), serverRequest.path());

        Mono<SalesTaxTrackingDto> salesTaxTrackingDtoMono = ContextLogger.observeCtx(logStr, log::info)
                .then(salesTaxTrackingDtoValidationHandler.validateQueryParam("country", country))
                .then(CountryIsUsaChecker.isCountryUsa(country) ? salesTaxTrackingDtoValidationHandler.validateQueryParam("state", state) : Mono.just(""))
                .then(Mono.defer(() -> salesTaxTrackingFacade.findByCountryAndState(country, state, subsidiary))
                        .flatMap(existingSalesTaxTracking -> serverRequest.bodyToMono(Map.class)
                                .map(map -> salesTaxTrackingPatcher.patch(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(existingSalesTaxTracking), map))
                                .switchIfEmpty(Mono.error(new InvalidPatchFieldException()))
                                .flatMap(salesTaxTrackingDto -> salesTaxTrackingDtoValidationHandler.handle(salesTaxTrackingDto, serverRequest.pathVariables().entrySet()))
                                .flatMap(salesTaxTrackingDto -> salesTaxTrackingFacade.update(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto), existingSalesTaxTracking))
                                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                                .flatMap(salesTaxTrackingDto -> ContextLogger.observeCtx("<-- Returned Body: " + salesTaxTrackingDto, log::info).thenReturn(salesTaxTrackingDto)))
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())));

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(salesTaxTrackingDtoMono, SalesTaxTrackingDto.class);
    }
}