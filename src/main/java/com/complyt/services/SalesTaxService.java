package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.sales_tax.SalesTax;

import java.util.List;

public interface SalesTaxService {
    SalesTax getSalesTax(Address address, List<Item> items);
}
