package com.complyt.v1.controllers.router.handler;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.security.permissions.sales_tax_tracking.SalesTaxTrackingReadPermission;
import com.complyt.security.permissions.sales_tax_tracking.SalesTaxTrackingUpdatePermission;
import com.complyt.v1.exceptions.ObjectNotFoundException;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.model.SalesTaxTrackingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "SalesTaxTracking", description = "This is the SalesTaxTracking controller")
public class SalesTaxTrackingHandler {

    @NonNull
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    @Operation(summary = "Gets SalesTaxTracking by id")
    @ResponseStatus(HttpStatus.OK)
    @SalesTaxTrackingReadPermission
    public Mono<ServerResponse> getOne(ServerRequest request) {
        String state = request.pathVariable("state");

        return ServerResponse.ok()
                .body(salesTaxTrackingFacade.findByState(state)
                        .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                        .switchIfEmpty(Mono.error(new ObjectNotFoundException("SalesTaxTracking with state " + state + " not found"))), SalesTaxTrackingDto.class);
    }

    @Operation(summary = "This will update the SalesTaxTracking if found by id, otherwise it will throw an error")
    @ResponseStatus(HttpStatus.OK)
    @SalesTaxTrackingUpdatePermission
    public Mono<ServerResponse> upsert(ServerRequest request) {
        String state = request.pathVariable("state");

        return request.bodyToMono(SalesTaxTrackingDto.class)
                .flatMap(salesTaxTrackingDto -> {
                    SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto);
                    return salesTaxTrackingFacade.findByState(state)
                            .flatMap(originalSalesTaxTracking -> salesTaxTrackingFacade.update(receivedSalesTaxTracking, state))
                            .flatMap(updatedSalesTaxTracking ->
                                    ServerResponse.status(HttpStatus.OK).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(updatedSalesTaxTracking)))
                            .switchIfEmpty(salesTaxTrackingFacade.save(receivedSalesTaxTracking)
                                    .flatMap(salesTaxTracking ->
                                            ServerResponse.status(HttpStatus.CREATED).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking))));
                });
    }

    @Operation(summary = "Gets all sales tax tracking")
    @ResponseStatus(HttpStatus.OK)
    @SalesTaxTrackingReadPermission
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(salesTaxTrackingFacade.findAll().map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto), SalesTaxTrackingDto.class);
    }
}
