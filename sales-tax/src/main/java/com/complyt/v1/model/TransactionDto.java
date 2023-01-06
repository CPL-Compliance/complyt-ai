package com.complyt.v1.model;

import com.complyt.annotations.Default;
import com.complyt.domain.Transaction;
import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.With;
import org.bson.types.ObjectId;

import java.util.List;
import java.util.UUID;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
@Schema(name = "Transaction")
public class TransactionDto extends ComplytEntityDto {
    private final String id;
    private final String externalId;
    private final String source;
    private final List<ItemDto> items;
    private final AddressDto billingAddress;
    private final AddressDto shippingAddress;
    private final ObjectId customerId;
    private final CustomerDto customer;
    private final SalesTaxDto salesTax;
    private final TransactionStatusDto transactionStatus;
    private final TimestampsDto internalTimestamps;
    private final TimestampsDto externalTimestamps;
    private final TransactionTypeDto transactionType;
    private final ShippingFeeDto shippingFee;
    private final String createdFrom;

    @Default
    public TransactionDto(final UUID complytId,final String id,final String externalId, final String source, final List<ItemDto> items, final AddressDto billingAddress, final AddressDto shippingAddress, final ObjectId customerId, final CustomerDto customer, final SalesTaxDto salesTax, final TransactionStatusDto transactionStatus, final TimestampsDto internalTimestamps, final TimestampsDto externalTimestamps, final TransactionTypeDto transactionType, final ShippingFeeDto shippingFee, final String createdFrom) {
        super(complytId);
        this.id = id;
        this.externalId = externalId;
        this.source = source;
        this.items = items;
        this.billingAddress = billingAddress;
        this.shippingAddress = shippingAddress;
        this.customerId = customerId;
        this.customer = customer;
        this.salesTax = salesTax;
        this.transactionStatus = transactionStatus;
        this.internalTimestamps = internalTimestamps;
        this.externalTimestamps = externalTimestamps;
        this.transactionType = transactionType;
        this.shippingFee = shippingFee;
        this.createdFrom = createdFrom;
    }

    @Override
    public ComplytEntityDto withComplytId(UUID complytId) {
        return this.complytId == complytId ?
                this : new TransactionDto(
                complytId,
                this.id,
                this.externalId,
                this.source,
                this.items,
                this.billingAddress,
                this.shippingAddress,
                this.customerId,
                this.customer,
                this.salesTax,
                this.transactionStatus,
                this.internalTimestamps,
                this.externalTimestamps,
                this.transactionType,
                this.shippingFee,
                this.createdFrom
        );
    }
}