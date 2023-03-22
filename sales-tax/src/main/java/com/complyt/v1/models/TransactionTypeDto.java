package com.complyt.v1.models;

import com.complyt.v1.api_info.FieldsDescriptions;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = FieldsDescriptions.TRANSACTION_TYPE)
public enum TransactionTypeDto {
    SALES_ORDER,
    INVOICE,
    ESTIMATE,
    REFUND
}