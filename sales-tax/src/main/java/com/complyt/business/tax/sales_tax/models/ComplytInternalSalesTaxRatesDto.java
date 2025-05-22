package com.complyt.business.tax.sales_tax.models;

import com.complyt.domain.sales_tax.FilingMetaData;
import com.complyt.domain.transaction.MatchedAddressData;
import lombok.With;

import java.util.UUID;

@With
public record ComplytInternalSalesTaxRatesDto(UUID complytId, MatchedAddressData matchedAddressData, InternalSalesTaxRatesDto salesTaxRates, FilingMetaData filingMetaData) {
}