package com.complyt.domain;

import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class FastTaxDataTest {

    private FastTaxData fastTaxData;
    private final String UNINCORPORATED_CODE = "1";

    @BeforeEach
    void setUp() {
        List<TaxInfoItem> taxInfoItemList = new ArrayList<>() {{
            add(new TaxInfoItem().withNotesCodes(UNINCORPORATED_CODE));
        }};
        fastTaxData = new FastTaxData().withTaxInfoItems(taxInfoItemList);
    }

    @Test
    void isUnincorporated_FastTaxDataIsUnincorporated_ReturnsTrue() {
        // Given

        // When
        boolean isUnincorporated = fastTaxData.isUnincorporated();

        // Then
        Assertions.assertTrue(isUnincorporated);
    }

    @Test
    void isUnincorporated_TaxInfoItemsIsNull_Returnsfalse() {
        // Given
        fastTaxData = fastTaxData.withTaxInfoItems(null);

        // When
        boolean isUnincorporated = fastTaxData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }

    @Test
    void isUnincorporated_FastTaxDataIsNotUnincorporated_ReturnsFalse() {
        // Given
        String INCORPORATED_CODE = "2";
        List<TaxInfoItem> taxInfoItemList = new ArrayList<>() {{
            add(new TaxInfoItem().withNotesCodes(INCORPORATED_CODE));
        }};
        fastTaxData = fastTaxData
                .withTaxInfoItems(taxInfoItemList);

        // When
        boolean isUnincorporated = fastTaxData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }




}