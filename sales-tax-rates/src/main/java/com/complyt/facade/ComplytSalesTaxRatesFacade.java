package com.complyt.facade;

import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.services.ComplytSalesTaxRatesService;
import lombok.NonNull;
import lombok.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Value
public class ComplytSalesTaxRatesFacade {

    @NonNull
    ComplytSalesTaxRatesService complytSalesTaxRatesServiceImpl;

    public Mono<ComplytSalesTaxRates> findByAddress(@NonNull Address address) {
        return complytSalesTaxRatesServiceImpl.findByAddress(address);
    }

}
