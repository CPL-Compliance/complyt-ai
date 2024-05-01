package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.business.address.CountryToStandardizedCountry;
import com.complyt.business.address.StateMap;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Transaction;
import org.springframework.stereotype.Component;

@Component
public class UsaAddressShippingAddressAligner implements ShippingAddressAligner {
    @Override
    public Transaction align(Transaction transaction) {
        String alignedCountry = CountryToStandardizedCountry.standardize(transaction.getShippingAddress().country());
        String alignedState = StateMap.statesToStandartizedState.get(transaction.getShippingAddress().state().toUpperCase());
        Address alignedShippingAddress = transaction.getShippingAddress()
                .withCountry(alignedCountry)
                .withState(alignedState);
        return transaction.withShippingAddress(alignedShippingAddress);
    }
}