package io.complyt.domain.transaction;

import io.complyt.domain.customer.Customer;
import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.sales_tax.SalesTax;
import io.complyt.domain.timestamps.Timestamps;
import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@With
public record Transaction(
        UUID complytId,
        String id,
        String externalId,
        String source,
        String documentName,
        List<Item> items,
        Boolean isTaxInclusive,
        Address billingAddress,
        ShippingAddress shippingAddress,
        UUID customerId,
        Customer customer,
        SalesTax salesTax,
        TransactionStatus transactionStatus,
        String tenantId,
        Timestamps internalTimestamps,
        Timestamps externalTimestamps,
        TransactionType transactionType,
        ShippingFee shippingFee,
        String createdFrom,
        BigDecimal taxableItemsAmount,
        BigDecimal tangibleItemsAmount,
        BigDecimal totalItemsAmount,
        BigDecimal finalTransactionAmount,
        BigDecimal totalDiscount,
        BigDecimal transactionLevelDiscount,
        TransactionFilingStatus transactionFilingStatus,
        String currency,
        BigDecimal refRate,
        ExchangeRateInfo exchangeRateInfo,
        String subsidiary,
        Boolean isRefundLinked,
        BigDecimal refundLinkedPercentage
) implements ComplytIdProperty {
}
