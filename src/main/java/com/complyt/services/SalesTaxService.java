package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.SalesTaxRate;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesTaxService {
    public Mono<SalesTaxData> findByAddress(Address address);
    public SalesTaxRate mapSalesTaxDataToRate(SalesTaxData salesTaxData);
    public SalesTax calculateSalesTax(List<Item> items);
    public List<Item> getRulesForItems(List<Item> items, SalesTaxRate salesTaxRate);
}
