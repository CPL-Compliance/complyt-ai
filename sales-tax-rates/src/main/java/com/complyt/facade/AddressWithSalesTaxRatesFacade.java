package com.complyt.facade;

import com.complyt.domain.Address;
import com.complyt.domain.AddressWithSalesTaxRates;
import com.complyt.services.AddressWithSalesTaxRatesService;
import lombok.NonNull;
import lombok.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Value
public class AddressWithSalesTaxRatesFacade {

    @NonNull
    AddressWithSalesTaxRatesService AddressWithSalesTaxRatesServiceImpl;

    public Mono<AddressWithSalesTaxRates> findByAddress(@NonNull Address address) {
        return AddressWithSalesTaxRatesServiceImpl.findByAddress(address);
    }

}
