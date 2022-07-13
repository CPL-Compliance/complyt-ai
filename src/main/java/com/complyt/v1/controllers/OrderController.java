package com.complyt.v1.controllers;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusTracking;
import com.complyt.facades.OrderFacade;
import com.complyt.security.permissions.order.OrderDeletePermission;
import com.complyt.security.permissions.order.OrderReadPermission;
import com.complyt.security.permissions.order.OrderUpdatePermission;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Slf4j
@Tag(name = "Order", description = "This is the Order controller")
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/v1/orders";

    @NonNull
    private OrderFacade orderFacade;

    @Operation(summary = "Gets order by externalId")
    @OrderReadPermission
    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> getOne(@PathVariable("externalId") @NonNull String externalId) {
        return orderFacade.findByExternalId(externalId)
                .map(orderItem -> new ResponseEntity<>(OrderMapper.INSTANCE.orderToOrderDto(orderItem), HttpStatus.OK))
                .switchIfEmpty(Mono.error(new NotFoundException(externalId)));
    }

    @Operation(summary = "Gets all orders")
    @OrderReadPermission
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Flux<OrderDto> getAll() {
        return orderFacade.getAll().map(OrderMapper.INSTANCE::orderToOrderDto);
    }

    @Operation(summary = "This will update the order if found by externalId, otherwise it will create it")
    @OrderUpdatePermission
    @PutMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> upsert(@PathVariable("externalId") @NonNull String externalId,
                                                 @RequestBody @NonNull OrderDto orderDto) {
        log.debug("Upsert order - DTO received in request body : " + orderDto);
        Order mappedOrder = OrderMapper.INSTANCE.orderDtoToOrder(orderDto);

        return orderFacade.findByExternalId(externalId)
                .flatMap(order -> orderFacade.updateIfModified(externalId, mappedOrder))
                .map(orderItem -> ResponseEntity.status(HttpStatus.OK).body(OrderMapper.INSTANCE.orderToOrderDto(orderItem))).log()
                .switchIfEmpty(orderFacade.saveOrder(mappedOrder)
                        .map(order -> ResponseEntity.status(HttpStatus.CREATED).body(OrderMapper.INSTANCE.orderToOrderDto(order)))).log();
    }

    @Operation(summary = "Marks the order as cancelled")
    @OrderDeletePermission
    @DeleteMapping("{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity> delete(@PathVariable("externalId") @NonNull String externalId) {
        log.debug("Delete order - external id received as path variable : " + externalId);

        return orderFacade.markAsCancelled(externalId).log().map(order -> ResponseEntity.noContent().build());
    }
}