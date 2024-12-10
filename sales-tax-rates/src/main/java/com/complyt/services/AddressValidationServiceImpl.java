package com.complyt.services;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.domain.Address;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AddressValidationServiceImpl implements AddressValidationService {

    @NonNull
    AddressValidationWebClientWrapper<Address> addressValidationWebClientWrapper;

    @Override
    public Mono<Address> validate(@NonNull Address address) {
        return addressValidationWebClientWrapper.validateAddress(address);
    }
}
