package com.complyt.v1.model.common_sales_tax_rates;

import com.complyt.domain.enums.SalesTaxSources;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.util.UUID;

@With
@Schema(name = "ComplytSalesTaxRates")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CommonSalesTaxRatesDto(UUID complytId, CommonAddressDto address, SalesTaxRatesDto salesTaxRates, SalesTaxSources source) {
}