package com.example.complyt.domain.fast_tax;

import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FastTaxDataTest {

    private FastTaxData fastTaxData;

    @BeforeEach
    void setUp() {
        fastTaxData = createFastTaxData();
    }

    private FastTaxData createFastTaxData() {
        String UNINCORPORATED_CODE = "1";
        TaxInfoItem taxInfoItem = TestUtilities.createTaxInfoItemWithNullValues().withNotesCodes(UNINCORPORATED_CODE);
        List<TaxInfoItem> taxInfoItemList = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        return new FastTaxData("lvl", taxInfoItemList);
    }

    @Test
    void isUnincorporated_FastTaxDataIsUnincorporated_ReturnTrue() {

        // Given + When
        boolean isUnincorporated = fastTaxData.isUnincorporated();

        // Then
        Assertions.assertTrue(isUnincorporated);
    }

    @Test
    void isUnincorporated_TaxInfoItemsIsNull_ReturnFalse() {
        // Given
        fastTaxData = fastTaxData.withTaxInfoItems(null);

        // When
        boolean isUnincorporated = fastTaxData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }

    @Test
    void isUnincorporated_FastTaxDataIsNotUnincorporated_ReturnFalse() {
        // Given
        String INCORPORATED_CODE = "2";
        TaxInfoItem taxInfoItem = TestUtilities.createTaxInfoItemWithNullValues().withNotesCodes(INCORPORATED_CODE);
        List<TaxInfoItem> taxInfoItemList = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        fastTaxData = fastTaxData
                .withTaxInfoItems(taxInfoItemList);

        // When
        boolean isUnincorporated = fastTaxData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "FastTaxData(matchLevel=" + fastTaxData.getMatchLevel() +
                ", taxInfoItems=" + fastTaxData.getTaxInfoItems() +
                ", UNINCORPORATED_CODE=" + fastTaxData.getUNINCORPORATED_CODE() + ")";

        // When
        String actualString = fastTaxData.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameFastTaxData_ReturnTrue() {
        // Given
        FastTaxData givenFastTaxData = createFastTaxData();

        // When
        boolean isEquals = fastTaxData.equals(givenFastTaxData);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void Builder_Build_ReturnFastTaxData() {
        // Given + When
        FastTaxData actualFastTaxData = new FastTaxData("lvl", fastTaxData.getTaxInfoItems());

        // Then
        assertEquals(fastTaxData, actualFastTaxData);
    }


}