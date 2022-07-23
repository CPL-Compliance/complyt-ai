package com.complyt.services;

import com.complyt.business.utils.date_injector.ModifiedOrderInternalDateInjector;
import com.complyt.business.utils.date_injector.NewOrderInternalDateInjector;
import com.complyt.domain.Order;
import com.complyt.domain.OrderStatus;
import com.complyt.repositories.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {

    @NonNull
    private OrderRepository orderRepository;

    @NonNull
    @Qualifier("productClassificationServiceImpl")
    private ProductClassificationService productClassificationService;

    @Override
    public Mono<Order> save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    public Mono<Order> findByExternalId(@NonNull String externalId) {
        return orderRepository.findByExternalId(externalId);
    }

    public Mono<Order> update(@NonNull final String externalId, @NonNull final Order order) {
        return orderRepository.findByExternalId(externalId).log()
                .switchIfEmpty(Mono.error(new NotFoundException("No Order with externalId " + externalId)))
                .map(createUpdateOrderFunction(order))
                .flatMap(orderRepository::save);
    }

    @Override
    public Mono<Order> injectDataToModifiedOrder(@NonNull Order order) {
        return productClassificationService.getOrderWithRelevantProductClassificationData(order);
//                .map(ModifiedOrderInternalDateInjector::new)
//                .map(dateInjector -> dateInjector.inject());
    }

    @Override
    public Mono<Order> injectDataToNewOrder(@NonNull Order order) {
        return productClassificationService.getOrderWithRelevantProductClassificationData(order);
//                .map(NewOrderInternalDateInjector::new)
//                .map(dateInjector -> dateInjector.inject());
    }

    @Override
    public Mono<Order> findById(String id) {
        return orderRepository.findById(id);
    }

    @Override
    public Mono<Order> markAsCancelled(String externalId) {
        return orderRepository
                .findByExternalId(externalId)
                .map(order -> order.withOrderStatus(OrderStatus.CANCELLED))
                .flatMap(orderRepository::save);
    }

    @Override
    public Flux<Order> getOrdersByQuery(@NonNull Query query) {
        return orderRepository.findAllByQuery(query);
    }

    public Flux<Order> findAll() {
        return orderRepository.findAll();
    }

    private Function<Order, Order> createUpdateOrderFunction(@NonNull final Order order) {
        return orderInfo -> orderInfo.withExternalId(order.getExternalId())
                .withItems(order.getItems())
                .withBillingAddress(order.getBillingAddress())
                .withShippingAddress(order.getShippingAddress())
                .withCustomerId(order.getCustomerId())
                .withSalesTax(order.getSalesTax())
                .withOrderStatus(order.getOrderStatus());
    }
}
