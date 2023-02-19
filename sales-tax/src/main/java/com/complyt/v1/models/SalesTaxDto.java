package com.complyt.v1.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@With
@Schema(name = "SalesTax")
public record SalesTaxDto(
        @PositiveOrZero(message = "Amount can not be a negative number") float amount,
        SalesTaxRateDto salesTaxRate) {
}
