package com.complyt.business.data_injector;

import com.complyt.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import reactor.core.publisher.Mono;

@Getter
@AllArgsConstructor
public class AddressCountyInjector implements DataInjector<Address, String> {

    @NonNull
    Address address;

    @Override
    public Mono<Address> inject(@NonNull String county) {
        return Mono.just(address.withCounty(county));
    }

}
