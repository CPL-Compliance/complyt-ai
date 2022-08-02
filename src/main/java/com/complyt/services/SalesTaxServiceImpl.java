package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
@Slf4j
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    private SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRate;

    @NonNull
    private SalesTaxCalculator salesTaxCalculator;

    @NonNull
    private SalesTaxRateCalculator salesTaxRateCalculator;

    @Override
    public Mono<Order> handleSalesTaxCalculation(@NonNull Order order, @NonNull SalesTaxTracking salesTaxTracking) {
        boolean isApplied = LocalDateTime.now().compareTo(salesTaxTracking.getAppliedDate()) >= 0;

        return salesTaxTracking.isEnforcesSalesTax() && isApplied ? calculate(order) : Mono.just(order);
    }

    @Override
    public Mono<Order> calculate(@NonNull Order order) {
        return findByAddress(order.getShippingAddress())
                .map(this::salesTaxDataToSalesTaxRate)
                .map(injectSalesTaxToOrder(order));
    }

    @Override
    public float calculateSalesTaxAmount(List<Item> items) {
        return salesTaxCalculator.calculate(items);
    }

    private Mono<SalesTaxData> findByAddress(Address address) {
        return salesTaxWebClientWrapper.findByAddress(address);
    }

    private SalesTaxRate salesTaxDataToSalesTaxRate(SalesTaxData salesTaxData) {
        return salesTaxDataToSalesTaxRate.map(salesTaxData);
    }

    private Function<SalesTaxRate, Order> injectSalesTaxToOrder(Order order) {
        return salesTaxRate -> {
            log.info("Setting sales tax rates for order's items");
            List<Item> itemsWithRates = setSalesTaxRatesForItems(order.getItems(), salesTaxRate);
            Order orderWithItemsWithRates = order.withItems(itemsWithRates);

            log.info("Calculating total sales tax amount for order");
            float salesTaxAmount = calculateSalesTaxAmount(orderWithItemsWithRates.getItems());
            SalesTax salesTax = new SalesTax(salesTaxAmount, salesTaxRate);

            log.debug("Order's sales tax : " + salesTax);
            return orderWithItemsWithRates.withSalesTax(salesTax);
        };
    }

    private List<Item> setSalesTaxRatesForItems(List<Item> items, SalesTaxRate salesTaxRate) {
        return items.stream()
                .map(item -> item.withSalesTaxRate(salesTaxRateCalculator.calculateSalesTaxRate(item.getJurisdictionalSalesTaxRules(), salesTaxRate)))
                .collect(Collectors.toList());
    }
}
