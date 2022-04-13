package com.complyt.v1.controllers;

import com.complyt.domain.Customer;
import com.complyt.domain.Order;
import com.complyt.facades.OrderFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.mappers.CustomerMapper;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.CustomerDto;
import com.complyt.v1.model.OrderDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/v1/orders";

    @NonNull
    private OrderFacade orderFacade;

//    @PutMapping("")
//    public Order createOrder(@RequestBody OrderDto orderDto) {
//        Order createdOPrder = orderFacade.create(OrderMapper.INSTANCE.orderDtoToOrder(orderDto));
//
//        return createdOPrder;
//    }

    @PutMapping("")
    public Mono<OrderDto> upsert(@RequestBody OrderDto orderDto) {
        try {
            Order order = OrderMapper.INSTANCE.orderDtoToOrder(orderDto);
            Mono<Order> orderMono = orderFacade.upsert(order);

            return orderMono.map(orderItem -> OrderMapper.INSTANCE.orderToOrderDto(orderItem));
        } catch (OperationFailedException operationFailedException) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, orderDto.toString(), operationFailedException);
        }
    }
}
