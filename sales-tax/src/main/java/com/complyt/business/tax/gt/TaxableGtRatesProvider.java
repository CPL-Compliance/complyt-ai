package com.complyt.business.tax.gt;

import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;

public interface TaxableGtRatesProvider<T> {
    T setGtRates(T t, GtRates gtRates, GtAddress gtAddress);
}
