package com.complyt.services;

import com.complyt.domain.Address;
import com.complyt.domain.Item;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTax;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SalesTaxService {
    SalesTax getSalesTaxSync(Address address, List<Item> items);
}
