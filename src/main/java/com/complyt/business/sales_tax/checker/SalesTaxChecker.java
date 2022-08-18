package com.complyt.business.sales_tax.checker;

import lombok.NonNull;

public interface SalesTaxChecker<T> {
    boolean check(@NonNull T t);
}
