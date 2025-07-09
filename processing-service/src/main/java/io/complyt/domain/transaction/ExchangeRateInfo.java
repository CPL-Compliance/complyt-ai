package io.complyt.domain.transaction;


import io.complyt.domain.currency.CurrencySource;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@With
public record ExchangeRateInfo(BigDecimal totalItemsAmountInUsd,
                               BigDecimal transactionSalesTaxInUsd,
                               BigDecimal finalTransactionAmountInUsd,
                               String fromCurrency,
                               String toCurrency,
                               BigDecimal fxRate,
                               CurrencySource source,
                               Boolean isExchangeRateEstimated,
                               LocalDateTime exchangeRateDate) {
}