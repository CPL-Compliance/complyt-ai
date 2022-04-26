package com.complyt.services;

import com.complyt.business.sales_tax.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.FastTaxDataToSalesTaxRateMapper;
import com.complyt.domain.sales_tax.mappers.FastTaxDataToSalesTaxRateMapperImpl;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
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
    public SalesTax getSalesTax(Address address, List<Item> items) {
        Mono<SalesTaxData> monoSalesTaxData = null;
        try {
            monoSalesTaxData = salesTaxWebClientWrapper.findByAddress(address);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Mono<SalesTaxRate> monoSalesTaxRate = monoSalesTaxData.map(item -> salesTaxDataToSalesTaxRateMapper.map(item));

        return salesTaxCalculator.calculate(monoSalesTaxRate, items);
    }
}