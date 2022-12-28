package com.complyt.v1.controllers.router.handler;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.security.permissions.sales_tax_tracking.SalesTaxTrackingCreatePermission;
import com.complyt.security.permissions.sales_tax_tracking.SalesTaxTrackingReadPermission;
import com.complyt.security.permissions.sales_tax_tracking.SalesTaxTrackingUpdatePermission;
import com.complyt.services.nexus.SalesTaxTrackingService;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.model.SalesTaxTrackingDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@AllArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "SalesTaxTracking", description = "This is the SalesTaxTracking controller")
public class SalesTaxTrackingHandler {

    @NonNull
    @Qualifier("salesTaxTrackingServiceImpl")
    private SalesTaxTrackingService salesTaxTrackingService;

    @Operation(summary = "Gets SalesTaxTracking by id")
    @ResponseStatus(HttpStatus.OK)
    @SalesTaxTrackingReadPermission
    public Mono<ServerResponse> getOne(ServerRequest request) {
        String state = request.pathVariable("state");

        return ServerResponse.ok()
                .body(salesTaxTrackingService.findByState(state)
                        .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "SalesTaxTracking with state " + state + " not found"))), SalesTaxTrackingDto.class);
    }

    @Operation(summary = "This will update the SalesTaxTracking if found by id, otherwise it will throw an error")
    @ResponseStatus(HttpStatus.OK)
    @SalesTaxTrackingUpdatePermission
    public Mono<ServerResponse> update(ServerRequest request) {
        String state = request.pathVariable("state");

        return request.bodyToMono(SalesTaxTrackingDto.class)
                .flatMap(salesTaxTrackingDto -> {
                    SalesTaxTracking receivedSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto);
                    return salesTaxTrackingService.findByState(state)
                            .flatMap(originalSalesTaxTracking -> salesTaxTrackingService.update(receivedSalesTaxTracking, state))
                            .flatMap(updatedSalesTaxTracking ->
                                    ServerResponse.status(HttpStatus.OK).bodyValue(SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(updatedSalesTaxTracking)))
                            .switchIfEmpty(salesTaxTrackingService.save(receivedSalesTaxTracking)
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
                .body(salesTaxTrackingService.findAll().map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto), SalesTaxTrackingDto.class);
    }
}
