package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesTaxService {
    Mono<SalesTax> getSalesTax(Address address, List<Item> items);
}
