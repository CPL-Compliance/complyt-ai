package io.complyt.business.address;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryStandardizedMapTest {

    @Test
    void standardize_knownCountryInLowerCase_returnsStandardizedName() {
        String result = CountryStandardizedMap.standardize("mexico");
        assertEquals("Mexico", result);
    }

    @Test
    void standardize_knownCountryCode_returnsStandardizedName() {
        String result = CountryStandardizedMap.standardize("MX");
        assertEquals("Mexico", result);
    }

    @Test
    void standardize_knownCountryInNativeLanguage_returnsStandardizedName() {
        String result = CountryStandardizedMap.standardize("México");
        assertEquals("Mexico", result);
    }

    @Test
    void standardize_unknownCountry_returnsOriginalInput() {
        String input = "Wakanda";
        String result = CountryStandardizedMap.standardize(input);
        assertEquals(input, result);
    }

    @Test
    void standardize_blankInput_returnsOriginalInput() {
        String input = "   ";
        String result = CountryStandardizedMap.standardize(input);
        assertEquals(input, result);
    }

    @Test
    void standardize_knownArabicCountry_returnsStandardizedName() {
        String result = CountryStandardizedMap.standardize("مصر");
        assertEquals("Egypt", result);
    }

    @Test
    void standardize_knownCyrillicCountry_returnsStandardizedName() {
        String result = CountryStandardizedMap.standardize("РОССИЯ");
        assertEquals("Russia", result);
    }
}