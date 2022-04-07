package com.complyt.v1.controllers;

import com.complyt.domain.Order;
import com.complyt.facades.OrderFacade;
import com.complyt.v1.mappers.OrderMapper;
import com.complyt.v1.model.OrderDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@RestController
@RequestMapping(OrderController.BASE_URL)
public class OrderController {
    public static final String BASE_URL = "/v1/order";

    @NonNull
    private OrderFacade orderFacade;

    @PutMapping("")
    public Mono<OrderDto> createOrder(@RequestBody OrderDto orderDto) {
        return orderFacade
                .create(OrderMapper.INSTANCE.orderDtoToOrder(orderDto))
                .map(orderItem ->
                        OrderMapper.INSTANCE.orderToOrderDto(orderItem));
    }
}