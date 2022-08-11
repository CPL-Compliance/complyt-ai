package com.complyt.business.utils.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.Transaction;
import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class TransactionZipTaxCountyFetcher implements CountyFetcher {

    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    @Override
    public Mono<Transaction> fetch(Transaction transaction) {
        return salesTaxWebClientWrapper.findByAddress(transaction.getShippingAddress())
                .map(salesTaxData -> {
                    ZipTaxData zipTaxData = (ZipTaxData) salesTaxData;
                    String countyFromZipTax = zipTaxData.getResults().get(0).getGeoCounty();
                    Address shippingAddress = transaction.getShippingAddress();
                    return transaction.withShippingAddress(shippingAddress.withCounty(countyFromZipTax));
                });
    }
}
