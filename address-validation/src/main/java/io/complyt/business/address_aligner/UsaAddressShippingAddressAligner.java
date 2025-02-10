package io.complyt.business.address_aligner;

import io.complyt.business.address.CountryToStandardizedCountry;
import io.complyt.domain.Address;
import io.complyt.v1.validators.address_body_checks.StateExistsChecker;
import org.springframework.stereotype.Component;

@Component
public class UsaAddressShippingAddressAligner implements AddressAligner {
    @Override
    public Address align(Address address) {
        String alignedCountry = CountryToStandardizedCountry.standardize(address.country().trim());
        String alignedState = StateExistsChecker.check(address.state());

        return address
                .withCountry(alignedCountry)
                .withState(alignedState);
    }
}