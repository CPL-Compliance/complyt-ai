package com.complyt.business.transaction.data_fetcher;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.CityCountyWrapper;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@AllArgsConstructor
@Component
public class TransactionCityCountyFetcher implements CityCountyFetcher {

    @NonNull
    AddressValidationWebClientWrapper<Address> complytAddressValidationWebClientWrapper;

    @Override
    public Mono<CityCountyWrapper> fetch(Address address) {
        return complytAddressValidationWebClientWrapper.validateAddress(address)
                .map(validatedAddress -> new CityCountyWrapper(validatedAddress.city(), validatedAddress.county()));
    }
}