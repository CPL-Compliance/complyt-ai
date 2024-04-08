package com.complyt.v1.models.sales_tax.gt;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@With
@Schema(name = "GtAddress")
public record GtAddressDto(String country, String region) {
}