package com.complyt.v1.models.tax.global_tax;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

@With
@Schema(name = "GtAddress")
public record GtAddressDto(String country, String region) {
}