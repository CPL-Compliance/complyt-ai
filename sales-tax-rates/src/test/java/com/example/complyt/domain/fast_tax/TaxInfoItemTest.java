package com.example.complyt.domain.fast_tax;

import com.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaxInfoItemTest {
    private TaxInfoItem taxInfoItem;

    @BeforeEach
    void setUp() {
        taxInfoItem = createTaxInfoItem();
    }

    private TaxInfoItem createTaxInfoItem() {
        return new TaxInfoItem("city", "", "", "", "", "", null, "", "", "", "", "", "", "", "", "");
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TaxInfoItem[city=" + taxInfoItem.city() +
                ", cityDistrictRate=" + taxInfoItem.cityDistrictRate() +
                ", cityRate=" + taxInfoItem.cityRate() +
                ", county=" + taxInfoItem.county() +
                ", countyDistrictRate=" + taxInfoItem.countyDistrictRate() +
                ", countyRate=" + taxInfoItem.cityRate() +
                ", informationComponents=" + taxInfoItem.informationComponents() +
                ", notesCodes=" + taxInfoItem.notesCodes() +
                ", notesDesc=" + taxInfoItem.notesDesc() +
                ", specialDistrictRate=" + taxInfoItem.specialDistrictRate() +
                ", stateAbbreviation=" + taxInfoItem.stateAbbreviation() +
                ", stateName=" + taxInfoItem.stateName() +
                ", stateRate=" + taxInfoItem.stateRate() +
                ", taxRate=" + taxInfoItem.taxRate() +
                ", totalTaxExempt=" + taxInfoItem.totalTaxExempt() +
                ", zip=" + taxInfoItem.zip() + "]";

        // When
        String actualString = taxInfoItem.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameTaxInfoItem_ReturnTrue() {
        // Given
        TaxInfoItem givenTaxInfoItem = createTaxInfoItem();

        // When
        boolean isEquals = taxInfoItem.equals(givenTaxInfoItem);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void Builder_Build_ReturnTaxInfoItemTest() {
        // Given + When
        TaxInfoItem actualTaxInfoItem = new TaxInfoItem(
                "city","","","",
                "", "",null,
                "","", "","",
                "","", "","","");

        // Then
        assertEquals(taxInfoItem, actualTaxInfoItem);
    }

}