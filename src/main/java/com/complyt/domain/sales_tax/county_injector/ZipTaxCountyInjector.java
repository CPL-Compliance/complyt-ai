package com.complyt.domain.sales_tax.county_injector;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTaxData;
import org.springframework.stereotype.Component;

@Component
public class ZipTaxCountyInjector implements CountyInjector{

    @Override
    public Order inject(Order order, SalesTaxData salesTaxData) {
        return null;
    }
}
