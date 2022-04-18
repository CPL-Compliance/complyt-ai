package com.complyt.v1.controllers;

import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import com.complyt.facades.OrderFacade;

import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.CustomerDto;
import com.complyt.v1.model.OrderDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/v1/orders";

    @NonNull
    private OrderFacade orderFacade;

    @PutMapping("")
    public Mono<ResponseEntity<OrderDto>> update(@RequestBody OrderDto orderDto) {
        Order order = OrderMapper.INSTANCE.orderDtoToOrder(orderDto);
        Mono<Order> orderMono = orderFacade.upsert(order);

        return orderMono.map(orderItem -> new ResponseEntity<>(OrderMapper.INSTANCE.orderToOrderDto(orderItem), HttpStatus.OK))
                .onErrorReturn(new ResponseEntity<>(orderDto, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @GetMapping("findByExternalId")
    public Mono<ResponseEntity<OrderDto>> getOrderByExternalId(@RequestParam String externalId) {
        return orderFacade.findByExternalId(externalId)
                .map(orderItem -> new ResponseEntity<>(OrderMapper.INSTANCE.orderToOrderDto(orderItem), HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public Flux<OrderDto> getAllOrders() {
        Flux<Order> orders = orderFacade.getAllOrders();

        return orders.map(item -> OrderMapper.INSTANCE.orderToOrderDto(item));
    }
}