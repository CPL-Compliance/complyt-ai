package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesTaxService {
    public Mono<SalesTaxData> findByAddress(Address address);
    public SalesTaxRate salesTaxDataToSalesTaxRate(SalesTaxData salesTaxData);
    public float calculateSalesTaxAmount(List<Item> items);
    public List<Item> setSalesTaxRatesForItems(List<Item> items, SalesTaxRate salesTaxRate);
}
