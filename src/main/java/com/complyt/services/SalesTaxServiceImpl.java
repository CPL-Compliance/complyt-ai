package com.complyt.services;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.business.tax_reliefs.JurisdictionalSalesTaxController;
import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.business.sales_tax.SalesTaxCalculator;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.mappers.SalesTaxDataToSalesTaxRateMapper;
import com.complyt.v1.model.ItemDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


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
    private final JurisdictionalSalesTaxController jurisdictionalSalesTaxController;

    @Override
    public Mono<SalesTaxData> findByAddress(Address address){
        return salesTaxWebClientWrapper.findByAddress(address);
    }

    @Override
    public SalesTaxRate mapSalesTaxDataToRate(SalesTaxData salesTaxData){
        return salesTaxDataToSalesTaxRateMapper.map(salesTaxData);
    }

    @Override
    public List<Item> getRulesForItems(List<Item> items, SalesTaxRate salesTaxRate){
        List<Item> itemsWithRates = new ArrayList<>();
        for(Item item: items){
            SalesTaxRate salesTaxRateForItem = jurisdictionalSalesTaxController.getRateByRules(item.getJurisdictionalSalesTaxRules(),salesTaxRate,item);
            itemsWithRates.add(item.withSalesTaxRate(salesTaxRateForItem));
        }
        return itemsWithRates;
    }

    @Override
    public float calculateSalesTaxAmount(List<Item> items){
        return salesTaxCalculator.calculate(items);
    }


}