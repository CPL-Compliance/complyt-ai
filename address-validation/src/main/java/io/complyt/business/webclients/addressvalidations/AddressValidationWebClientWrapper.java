package io.complyt.business.webclients.addressvalidations;

import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.here.HereAddressData;
import reactor.core.publisher.Mono;

public interface AddressValidationWebClientWrapper {
    Mono<AddressData> validateAddress(Address address);
}
