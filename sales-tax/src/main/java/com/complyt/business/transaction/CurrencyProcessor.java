package com.complyt.business.transaction;

import com.complyt.domain.currency.CurrencySource;
import com.complyt.domain.transaction.Transaction;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface CurrencyProcessor {
    String usdCurrency = "USD";

    static Boolean isUsdCurrency(String currency) {
        return usdCurrency.equals(currency);
    }

    static Boolean isFutureExternalCreatedDate(Transaction transaction) {
        return transaction.getExternalTimestamps().getCreatedDate().isAfter(LocalDateTime.now()) && transaction.getRefRate() == null;
    }

    static LocalDateTime getExchangeRateDate(Transaction transaction) {
        boolean isFutureExternalCreatedDate = transaction.getExternalTimestamps().getCreatedDate().isAfter(LocalDateTime.now()) && transaction.getRefRate() == null;
        return isFutureExternalCreatedDate ? LocalDate.now().atStartOfDay() : transaction.getExternalTimestamps().getCreatedDate();
    }

    static CurrencySource getExchangeSource(Transaction transaction) {
        return transaction.getRefRate() != null ?
                CurrencySource.CLIENT : CurrencySource.COMPLYT;
    }
}
