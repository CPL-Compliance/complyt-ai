package com.complyt.business.utils.transaction_data_injector;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@EqualsAndHashCode
public class FastTaxCountyInjector implements CountyInjector {

    @Autowired
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Override
    public Mono<Transaction> inject(Transaction transaction) {
        return salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())
                .map(salesTaxData -> {
                    FastTaxData fastTaxData = (FastTaxData) salesTaxData;
                    String countyFromFastTax = fastTaxData.getTaxInfoItems().get(0).getCounty();
                    Address shippingAddress = transaction.getShippingAddress();
                    return transaction.withShippingAddress(shippingAddress.withCounty(countyFromFastTax));
                });
    }
}
