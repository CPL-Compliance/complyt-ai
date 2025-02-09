package com.complyt.business.vat_validation;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class VatValidationAlignerTest {

    @Test
    void removeCountryCodeFromVatNumberIfPresent_VatNumberContainsCountryCode_TrimCountryCodeFromVatNumber() {
        // Given
        String vatNumber = "BE12345";
        String countryCode = "BE";

        String expectedVatNumberResult = "12345";

        // When
        String resultVatNumber = VatValidationAligner.removeCountryCodeFromVatNumberIfPresent(countryCode, vatNumber);

        // Then
        assertEquals(expectedVatNumberResult, resultVatNumber);
    }

    @Test
    void removeCountryCodeFromVatNumberIfPresent_VatNumberDoesNotContainsCountryCode_ReturnSameCodeFromVatNumber() {
        // Given
        String vatNumber = "12345";
        String countryCode = "BE";

        String expectedVatNumberResult = "12345";

        // When
        String resultVatNumber = VatValidationAligner.removeCountryCodeFromVatNumberIfPresent(countryCode, vatNumber);

        // Then
        assertEquals(expectedVatNumberResult, resultVatNumber);
    }

    @Test
    void removeCountryCodeFromVatNumberIfPresent_VatNumberContainsCountryCodeAndCodeNotUpperCase_TrimCountryCodeFromVatNumber() {
        // Given
        String vatNumber = "BE12345";
        String countryCode = "be";

        String expectedVatNumberResult = "12345";

        // When
        String resultVatNumber = VatValidationAligner.removeCountryCodeFromVatNumberIfPresent(countryCode, vatNumber);

        // Then
        assertEquals(expectedVatNumberResult, resultVatNumber);
    }

    @Test
    void removeCountryCodeFromVatNumberIfPresent_VatNumberContainsCountryCodeAndNumberCodeNotUpperCase_TrimCountryCodeFromVatNumber() {
        // Given
        String vatNumber = "be12345";
        String countryCode = "BE";

        String expectedVatNumberResult = "12345";

        // When
        String resultVatNumber = VatValidationAligner.removeCountryCodeFromVatNumberIfPresent(countryCode, vatNumber);

        // Then
        assertEquals(expectedVatNumberResult, resultVatNumber);
    }

    @Test
    void removeCountryCodeFromVatNumberIfPresent_VatNumberContainsCountryCodeAndBothAreLowerCase_TrimCountryCodeFromVatNumber() {
        // Given
        String vatNumber = "be12345";
        String countryCode = "be";

        String expectedVatNumberResult = "12345";

        // When
        String resultVatNumber = VatValidationAligner.removeCountryCodeFromVatNumberIfPresent(countryCode, vatNumber);

        // Then
        assertEquals(expectedVatNumberResult, resultVatNumber);
    }

    @Test
    void alignCountryCode_VatCountryCodesMapContainsCountryName_ReturnSameCodeUpperCase() {
        // Given
        String countryCode = "Belgium";
        String expected = "BE";

        // When
        String result = VatValidationAligner.alignCountryCode(countryCode);

        // Then
        assertEquals(expected, result);
    }

    @Test
    void alignCountryCode_VatCountryCodesMapDoesNotContainCountryName_ReturnSameCodeUpperCase() {
        // Given
        String countryCode = "None";
        String expected = "NONE";

        // When
        String result = VatValidationAligner.alignCountryCode(countryCode);

        // Then
        assertEquals(expected, result);

    }

    @Test
    void alignCountryCode_VatCountryCodesMapDoesNotContainCountryNameBecauseItsTheCode_ReturnSameCodeUpperCase() {
        // Given
        String countryCode = "be";
        String expected = "BE";

        // When
        String result = VatValidationAligner.alignCountryCode(countryCode);

        // Then
        assertEquals(expected, result);
    }

    @Test
    void alignCountryName_CountryCodeExistInVatCountryCodesMap_ReturnCountryName() {
        // Given
        String countryCode = "BE";
        String expected = "Belgium";


        // When
        String result = VatValidationAligner.alignCountryName(countryCode);

        // Then
        assertEquals(expected, result);
    }

    @Test
    void alignCountryName_CountryCodeExistInVatCountryCodesMapButLowerCase_ReturnCountryName() {
        // Given
        String countryCode = "be";
        String expected = "Belgium";


        // When
        String result = VatValidationAligner.alignCountryName(countryCode);

        // Then
        assertEquals(expected, result);
    }

    @Test
    void alignCountryName_CountryCodeDoesNotExistInVatCountryCodesMap_ReturnReceivedValue() {
        // Given
        String countryCode = "None";
        String expected = "None";


        // When
        String result = VatValidationAligner.alignCountryName(countryCode);

        // Then
        assertEquals(expected, result);
    }
}
