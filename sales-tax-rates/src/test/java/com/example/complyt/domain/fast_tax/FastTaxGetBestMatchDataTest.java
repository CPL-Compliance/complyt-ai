package com.example.complyt.domain.fast_tax;

import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FastTaxGetBestMatchDataTest {

    private FastTaxGetBestMatchData fastTaxGetBestMatchData;

    @BeforeEach
    void setUp() {
        fastTaxGetBestMatchData = createFastTaxGetBestMatchData();
    }

    private FastTaxGetBestMatchData createFastTaxGetBestMatchData() {
        String UNINCORPORATED_CODE = "1";
        TaxInfoItem taxInfoItem = TestUtilities.createTaxInfoItemWithNullValues().withNotesCodes(UNINCORPORATED_CODE);
        List<TaxInfoItem> taxInfoItemList = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        return new FastTaxGetBestMatchData("lvl", taxInfoItemList, "1");
    }

    @Test
    void isUnincorporated_FastTaxGetBestMatchDataIsUnincorporated_ReturnTrue() {

        // Given + When
        boolean isUnincorporated = fastTaxGetBestMatchData.isUnincorporated();

        // Then
        Assertions.assertTrue(isUnincorporated);
    }

    @Test
    void isUnincorporated_TaxInfoItemsIsNull_ReturnFalse() {
        // Given
        fastTaxGetBestMatchData = fastTaxGetBestMatchData.withTaxInfoItems(null);

        // When
        boolean isUnincorporated = fastTaxGetBestMatchData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }

    @Test
    void isUnincorporated_FastTaxGetBestMatchDataIsNotUnincorporated_ReturnFalse() {
        // Given
        String INCORPORATED_CODE = "2";
        TaxInfoItem taxInfoItem = TestUtilities.createTaxInfoItemWithNullValues().withNotesCodes(INCORPORATED_CODE);
        List<TaxInfoItem> taxInfoItemList = new ArrayList<>() {{
            add(taxInfoItem);
        }};
        fastTaxGetBestMatchData = fastTaxGetBestMatchData
                .withTaxInfoItems(taxInfoItemList);

        // When
        boolean isUnincorporated = fastTaxGetBestMatchData.isUnincorporated();

        // Then
        Assertions.assertFalse(isUnincorporated);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "FastTaxGetBestMatchData(matchLevel=" + fastTaxGetBestMatchData.getMatchLevel() +
                ", taxInfoItems=" + fastTaxGetBestMatchData.getTaxInfoItems() +
                ", UNINCORPORATED_CODE=" + fastTaxGetBestMatchData.getUNINCORPORATED_CODE() + ")";

        // When
        String actualString = fastTaxGetBestMatchData.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameFastTaxGetBestMatchData_ReturnTrue() {
        // Given
        FastTaxGetBestMatchData givenFastTaxGetBestMatchData = createFastTaxGetBestMatchData();

        // When
        boolean isEquals = fastTaxGetBestMatchData.equals(givenFastTaxGetBestMatchData);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void Builder_Build_ReturnFastTaxGetBestMatchData() {
        // Given + When
        FastTaxGetBestMatchData actualFastTaxGetBestMatchData = new FastTaxGetBestMatchData("lvl", fastTaxGetBestMatchData.getTaxInfoItems(), "1");

        // Then
        assertEquals(fastTaxGetBestMatchData, actualFastTaxGetBestMatchData);
    }

}