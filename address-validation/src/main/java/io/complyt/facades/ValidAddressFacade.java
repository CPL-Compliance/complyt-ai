package io.complyt.facades;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
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

    public Mono<ValidatedAddress> validateAddress(@NonNull Address address) {
        return validAddressService.validateAddress(address);
    }

    public Mono<CachedAddressData> resolveAddress(@NonNull Address address) {
        return validAddressService.resolveAddress(address);
    }
}