package com.complyt.business.sales_tax.checker;

import lombok.NonNull;

public interface SalesTaxApplyChecker<T> {
    boolean check(@NonNull T t);
}
