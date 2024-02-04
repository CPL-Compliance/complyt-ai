package com.complyt.domain;

import java.math.BigDecimal;

public interface Discountable {
    BigDecimal getDiscount();
    BigDecimal getUnitPrice();
    BigDecimal getQuantity();
    Discountable withTotalPrice(BigDecimal totalPrice);
}
