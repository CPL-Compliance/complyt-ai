package com.complyt.domain.sales_tax.county_injector;

import com.complyt.domain.Address;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import org.springframework.stereotype.Component;

@Component
public class FastTaxCountyInjector implements CountyInjector {

    @Override
    public Order inject(Order order, SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = (FastTaxData) salesTaxData;
        String countyFromFastTax = fastTaxData.getTaxInfoItems().get(0).getCounty();
        Address shippingAddress = order.getShippingAddress();

        return order.withShippingAddress(shippingAddress.withCounty(countyFromFastTax));
    }
}
