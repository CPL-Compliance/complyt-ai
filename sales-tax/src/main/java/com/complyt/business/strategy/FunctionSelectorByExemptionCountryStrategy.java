package com.complyt.business.strategy;

import com.complyt.business.address.CountryIsUsaChecker;
import com.complyt.domain.customer.exemption.ExemptionWrapper;

import java.util.function.Function;

public abstract class FunctionSelectorByExemptionCountryStrategy implements StrategySelector<ExemptionWrapper> {

    /**
     * this function is the strategy to decided between working with salestax rates and gt rates
     * even though, when getting usa/non usa country, the result of this function will be identical
     * we don't want to couple the logic, so for every strategt decition, this function will be run
     *
     * @param exemptionWrapper - to be getting the country and parameters from, to inject into a function
     * @return a function, either with usa or non usa logic, with the transaction details injected into the logic
     */
    @Override
    public Function select(ExemptionWrapper exemptionWrapper) {
        return CountryIsUsaChecker.isCountryUsa(exemptionWrapper.exemption().getCountry()) ?
                getFunctionForUsaOption(exemptionWrapper) : getFunctionForNonUsaOption(exemptionWrapper);
    }

    protected abstract Function getFunctionForUsaOption(ExemptionWrapper exemptionWrapper);

    protected abstract Function getFunctionForNonUsaOption(ExemptionWrapper ExemptionWrapper);
}