package com.complyt.services;

import com.complyt.business.address_validation.AddressValidationWebClientWrapper;
import com.complyt.domain.Address;
import com.complyt.domain.matched_address.MatchedAddressData;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class AddressValidationServiceImpl implements AddressValidationService {

    @NonNull
    AddressValidationWebClientWrapper<MatchedAddressData> addressValidationWebClientWrapper;

    @Override
    public Mono<MatchedAddressData> validate(@NonNull Address address) {
        return addressValidationWebClientWrapper.validateAddress(address);
    }
}
