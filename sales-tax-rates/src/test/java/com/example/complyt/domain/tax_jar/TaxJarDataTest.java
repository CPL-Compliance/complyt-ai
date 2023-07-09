package com.example.complyt.domain.tax_jar;

import com.complyt.domain.taxjar.TaxJarData;
import com.taxjar.model.rates.Rate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TaxJarDataTest {

    private TaxJarData taxJarData;

    @BeforeEach
    void setUp() {
        Rate rate = new Rate();
        taxJarData = new TaxJarData(rate);
    }

    @Test
    void isUnincorporated_TaxJarDataIsUnincorporated_ReturnTrue() {

        // Given + When
        boolean isUnincorporated = taxJarData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }

}
