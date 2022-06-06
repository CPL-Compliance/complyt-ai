package com.complyt.facades;

import com.complyt.business.order.OrderProductClassificationInjector;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.services.OrderService;
import com.complyt.services.ProductClassificationService;
import com.complyt.services.SalesTaxService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;


@Component
@AllArgsConstructor
public class OrderFacade {
    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    @Qualifier("salesTaxServiceImpl")
    @NonNull
    private SalesTaxService salesTaxService;

    @Qualifier("productClassificationServiceImpl")
    @NonNull
    private ProductClassificationService productClassificationService;

    public Mono<Order> save(Order order) {
        return orderService.save(order);
    }

    public Mono<Order> upsert(@NonNull String externalId, Order order) {
        return orderService.upsert(externalId, order);
    }

    public Mono<Order> update(@NonNull String externalId, Order order) {
        return orderService.update(externalId, order);
    }

    public Mono<Order> findByExternalId(String externalId) {
        return orderService.findByExternalId(externalId);
    }

    public Flux<Order> getAll() {
        return orderService.findAll();
    }

    public Mono<Order> updateSalesTax(String externalId) {
        return orderService
                .findByExternalId(externalId)
                .map(OrderProductClassificationInjector::new)
                .flatMap(injectRulesToOrderItems())
                .flatMap(setSalesTaxToOrder())
                .flatMap(order -> orderService.update(externalId, order));
    }

    private Function<OrderProductClassificationInjector, Mono<Order>> injectRulesToOrderItems() {
        return orderProductClassificationInjector -> Flux.fromIterable(orderProductClassificationInjector.getOrder().getItems())
                .flatMap(item -> getClassification(item.getTaxCode()))
                .distinct()
                .collectList()
                .flatMap(orderProductClassificationInjector::act);
    }

    private Function<Order, Mono<Order>> setSalesTaxToOrder() {
        return order -> salesTaxService.findByAddress(order.getShippingAddress())
                .map(salesTaxData -> salesTaxService.mapSalesTaxDataToRate(salesTaxData))
                .map(injectSalesTaxToOrder(order));
    }

    private Function<SalesTaxRate, Order> injectSalesTaxToOrder(Order order) {
        return salesTaxRate -> {
            List<Item> itemsWithRates = salesTaxService.getSalesTaxRatesForItems(order.getItems(), salesTaxRate);
            Order orderWithUpdatedItems = order.withItems(itemsWithRates);
            float salesTaxAmount = salesTaxService.calculateSalesTaxAmount(orderWithUpdatedItems.getItems());
            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRate);
            return orderWithUpdatedItems.withSalesTax(salesTax);
        };
    }

    @SneakyThrows
    public Mono<ProductClassification> getClassification(String taxCode) {
        return productClassificationService.findOneByTaxCode(taxCode);
    }

    public Flux<ProductClassification> getAllpcs() {
        return productClassificationService.getAll();
    }

    public Mono<Order> markAsCancelled(String orderId) {
        return orderService.markAsCancelled(orderId);
    }

}