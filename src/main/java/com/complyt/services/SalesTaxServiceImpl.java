package com.complyt.services;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.sales_tax.SalesTaxRateCalculator;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class SalesTaxServiceImpl implements SalesTaxService {

    @NonNull
    private final SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @NonNull
    private final SalesTaxDataToSalesTaxRateMapper salesTaxDataToSalesTaxRateMapper;

    @NonNull
    private final SalesTaxCalculator salesTaxCalculator;

    @NonNull
    private final SalesTaxRateCalculator jurisdictionalSalesTaxController;

    @Override
    public Mono<SalesTaxData> findByAddress(Address address){
        return salesTaxWebClientWrapper.findByAddress(address);
    }

    @Override
    public SalesTaxRate salesTaxDataToSalesTaxRate(SalesTaxData salesTaxData){
        return salesTaxDataToSalesTaxRateMapper.map(salesTaxData);
    }

    @Override
    public List<Item> setSalesTaxRatesForItems(List<Item> items, SalesTaxRate salesTaxRate){
        return items.stream()
                .map(item -> item.withSalesTaxRate(jurisdictionalSalesTaxController.calculateSalesTaxRate(item.getJurisdictionalSalesTaxRules(),salesTaxRate)))
                .collect(Collectors.toList());
    }

    @Override
    public float calculateSalesTaxAmount(List<Item> items){
        return salesTaxCalculator.calculate(items);
    }
}
