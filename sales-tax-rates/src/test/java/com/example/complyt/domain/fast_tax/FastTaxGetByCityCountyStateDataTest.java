package com.example.complyt.domain.fast_tax;

import com.complyt.domain.fast_tax.FastTaxGetByCityCountyStateData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FastTaxGetByCityCountyStateDataTest {

    private FastTaxGetByCityCountyStateData fastTaxGetByCityCountyStateData;

    @BeforeEach
    void setUp() {
        fastTaxGetByCityCountyStateData = createFastTaxGetByCityCountyStateData();
        System.out.println(fastTaxGetByCityCountyStateData);

    }

    private FastTaxGetByCityCountyStateData createFastTaxGetByCityCountyStateData() {
        return new FastTaxGetByCityCountyStateData("city", "county", "countyFips", "state", "", "", "", "", "", "", "", "");
    }

    @Test
    void isUnincorporated_FastTaxGetBestMatchDataIsUnincorporated_ReturnTrue() {

        // Given + When
        boolean isUnincorporated = fastTaxGetByCityCountyStateData.isUnincorporated();

        // Then
        Assertions.assertTrue(isUnincorporated);
    }


    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "FastTaxGetByCityCountyStateData(city=" + fastTaxGetByCityCountyStateData.getCity() +
                ", county=" + fastTaxGetByCityCountyStateData.getCounty() +
                ", countyFips=" + fastTaxGetByCityCountyStateData.getCountyFips() +
                ", stateName=" + fastTaxGetByCityCountyStateData.getStateName() +
                ", stateAbbreviation=" + fastTaxGetByCityCountyStateData.getStateAbbreviation() +
                ", totalTaxRate=" + fastTaxGetByCityCountyStateData.getTotalTaxRate() +
                ", totalTaxExempt=" + fastTaxGetByCityCountyStateData.getStateAbbreviation() +
                ", stateRate=" + fastTaxGetByCityCountyStateData.getStateRate() +
                ", cityRate=" + fastTaxGetByCityCountyStateData.getCityRate() +
                ", countyRate=" + fastTaxGetByCityCountyStateData.getCountyRate() +
                ", countyDistrictRate=" + fastTaxGetByCityCountyStateData.getCountyDistrictRate() +
                ", cityDistrictRate=" + fastTaxGetByCityCountyStateData.getCountyDistrictRate() + ")";


        // When
        String actualString = fastTaxGetByCityCountyStateData.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameFastTaxGetBestMatchData_ReturnTrue() {
        // Given
        FastTaxGetByCityCountyStateData givenFastTaxGetBestMatchData = createFastTaxGetByCityCountyStateData();

        // When
        boolean isEquals = fastTaxGetByCityCountyStateData.equals(givenFastTaxGetBestMatchData);

        // Then
        assertTrue(isEquals);
    }

    @Test
    void Builder_Build_ReturnFastTaxGetBestMatchData() {
        // Given + When
        FastTaxGetByCityCountyStateData actualFastTaxGetBestMatchData = new FastTaxGetByCityCountyStateData("city", "county", "countyFips", "state", "", "", "", "", "", "", "", "");

        // Then
        assertEquals(fastTaxGetByCityCountyStateData, actualFastTaxGetBestMatchData);
    }

}
