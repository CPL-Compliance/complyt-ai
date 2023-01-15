package com.complyt.v1.model;

import com.complyt.annotations.Default;
import com.complyt.domain.Transaction;
import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.ui.context.support.UiApplicationContextUtils;

import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@ToString
@With
@Schema(name = "Transaction")
public class TransactionDto {
    private final UUID complytId;
    private final String externalId;
    private final String source;
    private final List<ItemDto> items;
    private final AddressDto billingAddress;
    private final AddressDto shippingAddress;
    private final UUID customerId;
    private final CustomerDto customer;
    private final SalesTaxDto salesTax;
    private final TransactionStatusDto transactionStatus;
    private final TimestampsDto internalTimestamps;
    private final TimestampsDto externalTimestamps;
    private final TransactionTypeDto transactionType;
    private final ShippingFeeDto shippingFee;
    private final String createdFrom;

}