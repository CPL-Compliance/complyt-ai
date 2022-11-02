package com.complyt.domain;

import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;

public interface ITaxAble {
    float calculateSalesTaxAmount();
    TaxableCategory getTaxableCategory();
    TangibleCategory getTangibleCategory();
    String getTaxCode();
}