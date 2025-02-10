package io.complyt.business.address_aligner;

import io.complyt.domain.Address;

public interface AddressAligner {
    Address align(Address transaction);
}
