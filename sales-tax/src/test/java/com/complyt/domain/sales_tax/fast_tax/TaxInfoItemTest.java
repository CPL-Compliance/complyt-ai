package com.complyt.domain.sales_tax.fast_tax;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TaxInfoItemTest {
    private TaxInfoItem taxInfoItem;
    private final String UNINCORPORATED_CODE = "1";

    @BeforeEach
    void setUp() {
        taxInfoItem = createTaxInfoItem();
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TaxInfoItem(city=city, cityDistrictRate=, cityRate=, county=, countyDistrictRate=, countyRate=, informationComponents=null, notesCodes=, notesDesc=, specialDistrictRate=, stateAbbreviation=, stateName=, stateRate=, taxRate=, totalTaxExempt=, zip=)";

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
        boolean actualBoolean = taxInfoItem.equals(givenTaxInfoItem);

        // Then
        assertTrue(actualBoolean);
    }

    private TaxInfoItem createTaxInfoItem() {
        return new TaxInfoItem("city", "", "", "", "", "", null, "", "", "", "", "", "", "", "", "");
    }

    @Test
    void Builder_build() {
        // Given + When
        TaxInfoItem actualTaxInfoItem = TaxInfoItem.builder().city("city").cityDistrictRate("").cityRate("").county("").countyDistrictRate("").countyRate("").informationComponents(null).notesCodes("").notesDesc("").specialDistrictRate("").stateAbbreviation("").stateName("").stateRate("").taxRate("").totalTaxExempt("").zip("").build();

        // Then
        assertEquals(taxInfoItem, actualTaxInfoItem);
    }

    @Test
    void noArgsConstructor_ReturnEmptyTaxInfoItem() {
        // Given
        TaxInfoItem expectedTaxInfoItem = TaxInfoItem.builder().city(null).cityDistrictRate(null).cityRate(null).county(null).countyDistrictRate(null).countyRate(null).informationComponents(null).notesCodes(null).notesDesc(null).specialDistrictRate(null).stateAbbreviation(null).stateName(null).stateRate(null).taxRate(null).totalTaxExempt(null).zip(null).build();

        // When
        TaxInfoItem actualTaxInfoItem = new TaxInfoItem();

        // Then
        assertEquals(expectedTaxInfoItem,actualTaxInfoItem);
    }

}