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
        String expectedString = "TaxInfoItem(city=" + taxInfoItem.getCity() +
                ", cityDistrictRate=" + taxInfoItem.getCityDistrictRate() +
                ", cityRate=" + taxInfoItem.getCityRate() +
                ", county=" + taxInfoItem.getCounty() +
                ", countyDistrictRate=" + taxInfoItem.getCountyDistrictRate() +
                ", countyRate=" + taxInfoItem.getCityRate() +
                ", informationComponents=" + taxInfoItem.getInformationComponents() +
                ", notesCodes=" + taxInfoItem.getNotesCodes() +
                ", notesDesc=" + taxInfoItem.getNotesDesc() +
                ", specialDistrictRate=" + taxInfoItem.getSpecialDistrictRate() +
                ", stateAbbreviation=" + taxInfoItem.getStateAbbreviation() +
                ", stateName=" + taxInfoItem.getStateName() +
                ", stateRate=" + taxInfoItem.getStateRate() +
                ", taxRate=" + taxInfoItem.getTaxRate() +
                ", totalTaxExempt=" + taxInfoItem.getTotalTaxExempt() +
                ", zip=" + taxInfoItem.getZip() + ")";

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
        TaxInfoItem actualTaxInfoItem = TaxInfoItem.builder().city("city").cityDistrictRate("").cityRate("").county("").countyDistrictRate("").countyRate("").informationComponents(null).notesCodes("").notesDesc("").specialDistrictRate("").stateAbbreviation("").stateName("").stateRate("").taxRate("").totalTaxExempt("").zip("").build();

        // Then
        assertEquals(taxInfoItem, actualTaxInfoItem);
    }

}