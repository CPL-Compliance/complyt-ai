package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;


@Service
@AllArgsConstructor
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    @NonNull
    SalesTaxCalculator salesTaxCalculator;

    @Override
    public Mono<SalesTax> getSalesTax(Address address, List<Item> items) {
        return salesTaxWebClientWrapper
                .findByAddress(address)
                .map(item -> {
                    SalesTaxRate salesTaxRate = salesTaxDataToSalesTaxRateMapper.map(item);
                    return salesTaxCalculator.calculate(salesTaxRate, items);
                });
    }
}