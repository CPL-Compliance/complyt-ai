package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import org.springframework.stereotype.Component;

@Component
public class NonUsaAddressShippingAddressAligner implements ShippingAddressAligner {
    @Override
    public Transaction align(Transaction transaction) {
        String alignedCountry = CountryToStandardizedCountry.standardize(transaction.getShippingAddress().country().trim());
        ShippingAddress alignedShippingAddress = transaction.getShippingAddress().withCountry(alignedCountry);
        return transaction.withShippingAddress(alignedShippingAddress);
    }
}