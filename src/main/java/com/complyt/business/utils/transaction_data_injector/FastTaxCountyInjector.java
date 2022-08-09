package com.complyt.business.utils.transaction_data_injector;

import com.complyt.domain.Address;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Component
@EqualsAndHashCode
public class FastTaxCountyInjector implements CountyInjector {

    @Override
    public Transaction inject(Transaction transaction, SalesTaxData salesTaxData) {
        FastTaxData fastTaxData = (FastTaxData) salesTaxData;
        String countyFromFastTax = fastTaxData.getTaxInfoItems().get(0).getCounty();
        Address shippingAddress = transaction.getShippingAddress();

        return transaction.withShippingAddress(shippingAddress.withCounty(countyFromFastTax));
    }
}
