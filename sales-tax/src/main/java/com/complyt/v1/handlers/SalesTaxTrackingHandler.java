package com.complyt.v1.handlers;

import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.security.permissions.sales_tax_tracking.NexusReadPermission;
import com.complyt.security.permissions.sales_tax_tracking.NexusUpdatePermission;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.SalesTaxTrackingDto;
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


    @NexusReadPermission
    public Mono<ServerResponse> getOne(ServerRequest request) {
        String state = request.pathVariable("state");

        return ServerResponse.ok()
                .body(salesTaxTrackingFacade.findByState(state)
                        .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())), SalesTaxTrackingDto.class);
    }

    @NexusReadPermission
    public Mono<ServerResponse> getByComplytId(ServerRequest request) {
        UUID complytId = UUID.fromString(request.pathVariable("complytId"));

        return ServerResponse.ok()
                .body(salesTaxTrackingFacade.findByComplytId(complytId)
                        .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())), SalesTaxTrackingDto.class);
    }

    @NexusUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest request) {
        String state = request.pathVariable("state");

        return salesTaxTrackingDtoValidationHandler.validateRequestBody(request)
                .flatMap(salesTaxTrackingDto -> salesTaxTrackingDtoValidationHandler.checkStateConflict(salesTaxTrackingDto, state))
                .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingDtoToSalesTaxTracking)
                .flatMap(receivedSalesTaxTracking ->
                        salesTaxTrackingFacade.findByState(state)
                                .flatMap(originalSalesTaxTracking -> salesTaxTrackingFacade.update(receivedSalesTaxTracking, originalSalesTaxTracking, state)
                                        .flatMap(updatedSalesTaxTracking -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(Mono.just(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(updatedSalesTaxTracking)), SalesTaxTrackingDto.class)))
                                .switchIfEmpty(salesTaxTrackingFacade.save(receivedSalesTaxTracking)
                                        .flatMap(salesTaxTracking ->
                                                ServerResponse.status(HttpStatus.CREATED).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking)))));
    }


    @NexusReadPermission
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(salesTaxTrackingFacade.findAll().map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto), SalesTaxTrackingDto.class);
    }
}
