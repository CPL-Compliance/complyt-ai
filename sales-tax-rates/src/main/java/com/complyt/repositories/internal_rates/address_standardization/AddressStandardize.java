package com.complyt.repositories.internal_rates.address_standardization;

import com.complyt.domain.Address;

public interface AddressStandardize {
    /**
     * Standardizes the given Address by removing or replacing certain parts of the city and county fields.
     *
     * @param address the Address to be standardized
     * @return a new Address object with standardized city and county fields
     */
    Address standardize(Address address);
}
