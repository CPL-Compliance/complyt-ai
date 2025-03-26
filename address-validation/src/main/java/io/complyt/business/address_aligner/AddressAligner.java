package io.complyt.business.address_aligner;

import io.complyt.domain.Address;
import lombok.NonNull;

public interface AddressAligner {
    Address alignForOutsource(@NonNull Address address);
    Address alignGlobalAddress(@NonNull Address address);
}
