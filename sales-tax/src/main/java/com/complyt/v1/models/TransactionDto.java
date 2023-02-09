package com.complyt.v1.models;

import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.fields.ComplytIdFieldModel;
import com.complyt.v1.models.fields.ExternalIdAndSourceFieldsModel;
import com.complyt.v1.models.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.With;

import java.util.List;
import java.util.UUID;


@With
@Schema(name = "Transaction")
public record TransactionDto(UUID complytId, String externalId, String source, List<ItemDto> items,
                             AddressDto billingAddress, AddressDto shippingAddress, UUID customerId,
                             CustomerDto customer, SalesTaxDto salesTax, TransactionStatusDto transactionStatus,
                             TimestampsDto internalTimestamps, TimestampsDto externalTimestamps,
                             TransactionTypeDto transactionType, ShippingFeeDto shippingFee, String createdFrom,
                             float taxableItemsAmount, float tangibleItemsAmount, float totalItemsAmount
) implements ComplytIdFieldModel, ExternalIdAndSourceFieldsModel {
}