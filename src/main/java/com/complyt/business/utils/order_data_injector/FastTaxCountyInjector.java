package com.complyt.business.utils.order_data_injector;

import com.complyt.domain.Address;
import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode
public class FastTaxCountyInjector implements CountyInjector {

    @Override
    public Order inject(Order order, SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = (FastTaxData) salesTaxData;
        String countyFromFastTax = fastTaxData.getTaxInfoItems().get(0).getCounty();
        Address shippingAddress = order.getShippingAddress();

        return order.withShippingAddress(shippingAddress.withCounty(countyFromFastTax));
    }
}
