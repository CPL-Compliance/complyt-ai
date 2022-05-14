package com.complyt.v1.controllers;

import com.complyt.facades.OrderFacade;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.java.Log;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.webjars.NotFoundException;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Log
@Tag(name = "Order", description = "This is the Order controller")
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/v1/orders";

    @NonNull
    private final OrderFacade orderFacade;

    @Operation(summary = "Gets order by externalId")
    @GetMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> getOne(@PathVariable("externalId") @NonNull String externalId) {
        return orderFacade.findByExternalId(externalId)
                .map(orderItem -> new ResponseEntity<>(OrderMapper.INSTANCE.orderToOrderDto(orderItem), HttpStatus.OK))
                .switchIfEmpty(Mono.error(new NotFoundException(externalId)));
    }

//    @Operation(summary = "Gets all orders")
//    @GetMapping("")
//    @ResponseStatus(HttpStatus.OK)
//    public Flux<EntityModel<OrderDto>> getAll() {
//        return orderFacade.getAll()
//                .map(order -> {
//                    EntityModel<OrderDto> entityModel = EntityModel.of(OrderMapper.INSTANCE.orderToOrderDto(order));
//                    Link link = WebMvcLinkBuilder.linkTo(methodOn(this.getClass()).getAll()).withSelfRel();
//                    entityModel.add(link);
//
//                    return entityModel;
//                });
//    }

    @Operation(summary = "This will update the order if found by externalId")
    @PutMapping("{externalId}")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> update(@PathVariable("externalId") @NonNull String externalId, @RequestBody @NonNull OrderDto orderDto) {
        return Mono.just(OrderMapper.INSTANCE.orderDtoToOrder(orderDto))
                .flatMap(order -> orderFacade.upsert(externalId, order))
                .map(order -> ResponseEntity.ok().body(OrderMapper.INSTANCE.orderToOrderDto(order)));
    }

    @PutMapping("{externalId}/salesTax")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> updateSalesTax(@PathVariable("externalId") @NonNull String externalId) {
        return orderFacade.updateSalesTax(externalId)
                .map(order -> ResponseEntity.ok().body(OrderMapper.INSTANCE.orderToOrderDto(order)));
    }

    @Operation(summary = "Marks the order as cancelled")
    @DeleteMapping("{externalId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity> delete(@PathVariable("externalId") @NonNull String externalId) {
        return orderFacade.markAsCancelled(externalId).map(order -> ResponseEntity.noContent().build());
    }
}