package com.complyt.facades;

import com.complyt.domain.SalesTaxData;
import com.complyt.services.SalesTaxService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class SalesTaxFacade {
    @NonNull
    private SalesTaxService salesTaxService;

    public Mono<SalesTaxData> findByAddress(String zip, String address, String city, String state) {
        return salesTaxService.findByAddress(zip, address, city, state);
    }
}