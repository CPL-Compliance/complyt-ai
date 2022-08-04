package com.complyt.business.utils.order_data_injector;

import com.complyt.domain.Order;
import com.complyt.domain.sales_tax.SalesTaxData;

public interface CountyInjector {
    Order inject(Order order, SalesTaxData salesTaxData);
}
