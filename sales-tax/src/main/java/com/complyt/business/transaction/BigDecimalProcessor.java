package com.complyt.business.transaction;

import java.math.BigDecimal;

public interface BigDecimalProcessor {
    static BigDecimal removeTrailingZeros(BigDecimal bigDecimal) {
        return new BigDecimal(bigDecimal.stripTrailingZeros().toPlainString());
    }
}
