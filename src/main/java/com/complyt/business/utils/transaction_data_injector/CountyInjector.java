package com.complyt.business.utils.transaction_data_injector;

import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxData;

public interface CountyInjector {
    Transaction inject(Transaction transaction, SalesTaxData salesTaxData);
}
