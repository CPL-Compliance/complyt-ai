package com.complyt.domain.currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CurrencyExchangeRateObject(String currency, LocalDateTime date, BigDecimal rate) {

}
