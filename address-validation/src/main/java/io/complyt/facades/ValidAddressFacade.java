package io.complyt.facades;

import io.complyt.domain.Address;
import io.complyt.services.ValidAddressService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Value
@AllArgsConstructor
public class ValidAddressFacade {

    @NonNull
    ValidAddressService validAddressService;

    public Mono<Address> validateAddress(@NonNull Address address) {
        return validAddressService.validateAddress(address);
    }
}