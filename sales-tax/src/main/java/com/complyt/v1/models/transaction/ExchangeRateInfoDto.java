package com.complyt.v1.models.transaction;

import com.complyt.domain.currency.CurrencySource;
import com.complyt.v1.api_info.FieldsDescriptions;
import com.complyt.v1.config.error_messages.NumericErrorMessages;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.With;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(name = "ExchangeRateDto", description = FieldsDescriptions.EXCHANGE_RATE_INFO)
@With
public record ExchangeRateInfoDto(
        @PositiveOrZero(message = "ExchangeRateInfoDto.totalItemsAmountInUsd " + NumericErrorMessages.NOT_NEGATIVE_ERROR) BigDecimal totalItemsAmountInUsd,
        BigDecimal transactionSalesTaxInUsd,
        @PositiveOrZero(message = "ExchangeRateInfoDto.finalTransactionAmountInUsd " + NumericErrorMessages.NOT_NEGATIVE_ERROR) BigDecimal finalTransactionAmountInUsd,
        String fromCurrency,
        String toCurrency,
        @PositiveOrZero(message = "ExchangeRateInfoDto.fxRate " + NumericErrorMessages.NOT_NEGATIVE_ERROR) BigDecimal fxRate,
        CurrencySource source,
        Boolean isExchangeRateEstimated,
        LocalDateTime exchangeRateDate
) {
}