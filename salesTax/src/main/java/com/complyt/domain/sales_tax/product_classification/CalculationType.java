package com.complyt.domain.sales_tax.product_classification;

public enum CalculationType {
    FIXED, // Replacing the default jurisdiction rate with a specific fixed rate
    PERCENTAGE // The default jurisdiction rate applied on partial amount of the item's total price
}
