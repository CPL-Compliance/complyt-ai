package com.complyt.business.utils.data_fetcher;

import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import com.complyt.domain.Transaction;
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
        return null;
    }
}
