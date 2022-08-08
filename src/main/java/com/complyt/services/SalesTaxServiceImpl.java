package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxApplyCheck;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.utils.order_data_injector.CountyInjector;
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

    @NonNull
    private SalesTaxApplyCheck salesTaxApplyCheck;

    @NonNull
    private CountyInjector countyInjector;

    @Override
    public Mono<Order> handleSalesTaxCalculation(@NonNull Order order, @NonNull SalesTaxTracking salesTaxTracking) {
        boolean isApplied = salesTaxApplyCheck.isApplied(order, salesTaxTracking);

        return isApplied ? injectCountyToOrderAndCalculate(order) : Mono.just(order);
    }

    @Override
    public Mono<Order> injectCountyToOrderAndCalculate(@NonNull Order order) {
        return salesTaxWebClientWrapper.findByAddress(order.getShippingAddress())
                .map(salesTaxData -> {
                    SalesTaxRate salesTaxRate = salesTaxDataToSalesTaxRate.map(salesTaxData);
                    Order orderWithCounty = countyInjector.inject(order, salesTaxData);
                    return injectSalesTaxToOrder(orderWithCounty).apply(salesTaxRate);
                });
    }

    private Function<SalesTaxRate, Order> injectSalesTaxToOrder(Order order) {
        return salesTaxRate -> {
            log.info("Setting sales tax rates for order's items");
            List<Item> itemsWithRates = setSalesTaxRatesForItems(order.getItems(), salesTaxRate);
            Order orderWithItemsWithRates = order.withItems(itemsWithRates);

            log.info("Calculating total sales tax amount for order");
            float salesTaxAmount = salesTaxCalculator.calculate(orderWithItemsWithRates.getItems());
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

