package com.complyt.domain;


import com.complyt.domain.common_rates.CommonRates;
import com.complyt.domain.enums.SalesTaxSources;
import com.complyt.domain.internal_rates.InternalSalesTaxRatesMetaData;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.With;

import java.util.UUID;

@With
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalesTaxRatesData(UUID complytId, AddressWithDate requestAddress, MatchedAddressData matchedAddressData, CommonRates salesTaxRates, SalesTaxSources source,
                                InternalSalesTaxRatesMetaData ratesMetaData) {
}
