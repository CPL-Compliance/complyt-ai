package com.complyt.facades;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.services.ClientService;
import com.complyt.services.CustomerService;
import com.complyt.services.OrderService;
import com.complyt.services.SalesTaxService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
public class OrderFacade {
    @Qualifier("customerServiceImpl")
    @NonNull
    private CustomerService customerService;

    @Qualifier("clientServiceImpl")
    @NonNull
    private ClientService clientService;

    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    @Qualifier("salesTaxServiceImpl")
    @NonNull
    private SalesTaxService salesTaxService;

    public Order save(Order order) {
        return orderService.save(order);
    }

    public Mono<Order> upsert(Order order) {
        return orderService.upsert(order);
    }

    public Mono<Order> findByExternalId(String externalId) {
        return orderService.findByExternalId(externalId);
    }

    public Flux<Order> getAll() {
        return orderService.findAll();
    }

    public Mono<Order> updateSalesTax(String id) {
        Order order = null;
        try {
            order = orderService.findByExternalId(id).toFuture().get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        SalesTax salesTax = salesTaxService.getSalesTax(order.getShippingAddress(), order.getItems());
        Order orderWithSalesTax = order.withSalesTax(salesTax);

        return orderService.update(orderWithSalesTax);
    }
}
