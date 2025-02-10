package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.validators.body_checkers.StateExistsChecker;
import org.springframework.stereotype.Component;

@Component
public class UsaAddressShippingAddressAligner implements ShippingAddressAligner {
    @Override
    public Transaction align(Transaction transaction) {
        String alignedCountry = CountryToStandardizedCountry.standardize(transaction.getShippingAddress().country().trim());
        String alignedState = StateExistsChecker.check(transaction.getShippingAddress().state());
        ShippingAddress alignedShippingAddress = transaction.getShippingAddress()
                .withCountry(alignedCountry)
                .withState(alignedState);
        return transaction.withShippingAddress(alignedShippingAddress);
    }
}