package com.complyt.domain.transaction;

import lombok.*;

import java.math.BigDecimal;

@With
public record Discount (
        BigDecimal discountAmount,
        boolean isPreTax,
        String discountName
) {
}