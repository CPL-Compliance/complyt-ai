package com.complyt.v1.model.gt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@With
@Schema(name = "ComplytGtRates")
public record ComplytGtRatesDto(GtAddressDto gtAddress, GtRatesDto gtRates) {
}
