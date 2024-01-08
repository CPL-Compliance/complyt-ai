package com.complyt.v1.models.transaction;

import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.NumberFormat;

import java.math.BigDecimal;

@Schema(name = "Discount", description = FieldsDescriptions.DISCOUNT)
public record DiscountDto(
        @NotNull(message = "discountAmount " + DtoErrorMessages.NOT_NULL_ERROR) BigDecimal discountAmount,
        boolean isPreTax,
        @Size(max = 256, message = "discount.discountName " + StringErrorMessages.MAX_256_ERROR) String discountName
) {

}
