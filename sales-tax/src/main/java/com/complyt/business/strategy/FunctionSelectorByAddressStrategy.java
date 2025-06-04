package com.complyt.business.strategy;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.domain.transaction.MandatoryAddress;

import java.util.function.Function;

public abstract class FunctionSelectorByAddressStrategy implements StrategySelector<MandatoryAddress> {

    /**
     * this function is the strategy to decided between working with sales tax rates and gt rates
     * even though, when getting usa/non usa country, the result of this function will be identical
     * we don't want to couple the logic, so for every strategy decision, this function will be run
     *
     * @param address - to be getting the address and parameters from to inject into a function
     * @return a function, either with usa or non usa logic, with the transaction details injected into the logic
     */
    @Override
    public Function select(MandatoryAddress address) {
        return CountryIsUsaChecker.isCountryUsa(address) ?
                getFunctionForUsaOption(address) : getFunctionForNonUsaOption(address);
    }

    protected abstract Function getFunctionForUsaOption(MandatoryAddress address);

    protected abstract Function getFunctionForNonUsaOption(MandatoryAddress address);
}