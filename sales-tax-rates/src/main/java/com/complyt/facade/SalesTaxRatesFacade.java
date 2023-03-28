package com.complyt.facade;

import com.complyt.domain.Address;
import com.complyt.domain.SalesTaxRates;
import com.complyt.services.SalesTaxRatesServiceImpl;
import lombok.NonNull;
import lombok.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Value
public class SalesTaxRatesFacade {

    @NonNull
    SalesTaxRatesServiceImpl salesTaxRatesServiceImpl;

    public Mono<SalesTaxRates> findByAddress(@NonNull Address address) {
        return salesTaxRatesServiceImpl.findByAddress(address);
    }

}