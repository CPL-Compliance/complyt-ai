package com.complyt.v1.models.customer.exemption;

import com.complyt.v1.api_info.FieldsDescriptions;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = FieldsDescriptions.exemption_type)
public enum ExemptionTypeDto {
    FULLY,
    PARTIALLY
}
