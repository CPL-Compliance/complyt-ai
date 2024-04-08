package com.complyt.business.strategy;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.domain.transaction.Transaction;

import java.util.function.Function;

public abstract class FunctionSelectorByAddressStrategy implements StrategySelector<Transaction> {

    /**
     * this function is the strategy to decided between working with salestax rates and gt rates
     * even though, when getting usa/non usa country, the result of this function will be identical
     * we don't want to couple the logic, so for every strategt decition, this function will be run
     *
     * @param transaction - to be getting the address and parameters from to inject into a function
     * @return a function, either with usa or non usa logic, with the transaction details injected into the logic
     */
    @Override
    public Function select(Transaction transaction) {
        return CountryIsUsaChecker.isCountryUsa(transaction.getShippingAddress()) ?
                getFunctionForUsaOption(transaction) : getFunctionForNonUsaOption(transaction);
    }

    protected abstract Function getFunctionForUsaOption(Transaction transaction);

    protected abstract Function getFunctionForNonUsaOption(Transaction transaction);
}