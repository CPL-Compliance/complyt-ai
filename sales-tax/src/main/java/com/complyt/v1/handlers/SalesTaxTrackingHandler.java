package com.complyt.v1.handlers;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.facades.SalesTaxTrackingFacade;
import com.complyt.security.permissions.sales_tax_tracking.NexusReadPermission;
import com.complyt.security.permissions.sales_tax_tracking.NexusUpdatePermission;
import com.complyt.v1.exceptions.types.ObjectNotFoundApiException;
import com.complyt.v1.mappers.SalesTaxTrackingMapper;
import com.complyt.v1.models.SalesTaxTrackingDto;
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
@Tag(name = "Nexus", description = "This is the Nexus controller")
public class SalesTaxTrackingHandler {

    @NonNull
    SalesTaxTrackingFacade salesTaxTrackingFacade;

    @Operation(summary = "Gets SalesTaxTracking by id")
    @ResponseStatus(HttpStatus.OK)
    @NexusReadPermission
    public Mono<ServerResponse> getOne(ServerRequest request) {
        String state = request.pathVariable("state");

        return ServerResponse.ok()
                .body(salesTaxTrackingFacade.findByState(state)
                        .map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto)
                        .switchIfEmpty(Mono.error(new ObjectNotFoundApiException())), SalesTaxTrackingDto.class);
    }

    @Operation(summary = "This will update the SalesTaxTracking if found by id, otherwise it will throw an error")
    @ResponseStatus(HttpStatus.OK)
    @NexusUpdatePermission
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
    @NexusReadPermission
    public Mono<ServerResponse> getAll(ServerRequest request) {
        return ServerResponse
                .ok()
                .body(salesTaxTrackingFacade.findAll().map(SalesTaxTrackingMapper.INSTANCE::salesTaxTrackingToSalesTaxTrackingDto), SalesTaxTrackingDto.class);
    }
}
