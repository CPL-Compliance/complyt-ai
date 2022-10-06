package com.complyt.domain;

import com.complyt.domain.sales_tax.zip_tax.ZipTaxData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ZipTaxDataTest {

    private ZipTaxData zipTaxData;

    @BeforeEach
    void setUp() {
        zipTaxData = new ZipTaxData();
    }

    @Test
    void isUnincorporated_ZipTaxDataIsNotUnincorporated_ReturnsFalse() {
        // Given

        // When
        boolean isUnincorporated = zipTaxData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }
}