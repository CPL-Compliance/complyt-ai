package com.complyt.facades;

import com.complyt.domain.sales_tax.SalesTaxData;
import com.complyt.business.sales_tax.sales_tax_web_clients.SalesTaxWebClientWrapper;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class SalesTaxFacade {
    @NonNull
    private SalesTaxWebClientWrapper salesTaxWebClientWrapper;

    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        return salesTaxWebClientWrapper.findByAddress(zip, address, city, state);
    }
}