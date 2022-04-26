package com.complyt.v1.controllers;

import com.complyt.domain.Order;
import com.complyt.facades.OrderFacade;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Tag(name = "Order", description = "This is the Order controller")
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/v1/orders";

    @NonNull
    private OrderFacade orderFacade;

    @Operation(summary = "This will update the order if found by externalId, otherwise it will create the customer")
    @PutMapping("")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> update(@RequestBody OrderDto orderDto) {
        Order order = OrderMapper.INSTANCE.orderDtoToOrder(orderDto);
        Mono<Order> orderMono = orderFacade.upsert(order);

        return orderMono
                .map(orderItem -> new ResponseEntity<>(OrderMapper.INSTANCE.orderToOrderDto(orderItem), HttpStatus.OK))
                .onErrorReturn(new ResponseEntity<>(orderDto, HttpStatus.INTERNAL_SERVER_ERROR));
    }


    @PutMapping("{id}/salesTax")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> updateSalesTax(@PathVariable String id) {
        Mono<Order> orderMono = orderFacade.setSalesTax(id);
        Order order = orderMono.block();
        OrderDto orderDto = OrderMapper.INSTANCE.orderToOrderDto(order);
        
        return orderMono
                .map(orderItem -> new ResponseEntity<>(OrderMapper.INSTANCE.orderToOrderDto(orderItem), HttpStatus.OK))
                .onErrorReturn(new ResponseEntity<>(orderDto, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @Operation(summary = "Gets order by externalId")
    @GetMapping("findByExternalId")
    @ResponseStatus(HttpStatus.OK)
    public Mono<ResponseEntity<OrderDto>> getByExternalId(@RequestParam String externalId) {
        return orderFacade.findByExternalId(externalId)
                .map(orderItem -> new ResponseEntity<>(OrderMapper.INSTANCE.orderToOrderDto(orderItem), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @Operation(summary = "Gets all the orders")
    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<OrderDto> getAll() {
        Flux<Order> orders = orderFacade.getAll();

        return orders.map(item -> OrderMapper.INSTANCE.orderToOrderDto(item));
    }
}