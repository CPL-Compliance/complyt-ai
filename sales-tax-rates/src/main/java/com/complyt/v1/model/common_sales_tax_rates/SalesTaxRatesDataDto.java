package com.complyt.v1.model.common_sales_tax_rates;


import com.complyt.domain.AddressWithDate;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesMetaDataDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "SalesTaxRatesData")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record SalesTaxRatesDataDto(UUID complytId, AddressWithDate requestAddress, MatchedAddressData matchedAddressData, SalesTaxRatesDto salesTaxRates, InternalSalesTaxRatesMetaDataDto ratesMetaData) {
}
