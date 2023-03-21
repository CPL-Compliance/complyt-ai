package com.complyt.v1.models.customer;

import com.complyt.v1.api_info.FieldsDescriptions;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = FieldsDescriptions.customer_type)
public enum CustomerTypeDto {
    RETAIL,
    MARKETPLACE,
    RESELLER,
    RETAIL_EXEMPT
}
