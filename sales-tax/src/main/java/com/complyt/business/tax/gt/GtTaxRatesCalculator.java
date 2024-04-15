package com.complyt.business.tax.gt;

import com.complyt.domain.transaction.tax.GtRates;
import lombok.NonNull;

public interface GtTaxRatesCalculator<T> {
    GtRates calculate(@NonNull T t, @NonNull GtRates originalSalesTaxRate);
}
