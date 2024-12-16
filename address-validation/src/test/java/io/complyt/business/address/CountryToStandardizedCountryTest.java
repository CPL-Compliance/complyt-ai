package io.complyt.business.address;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountryToStandardizedCountryTest {
    @Test
    void standardize_whenCountryIsUsaVariant_returnsStandardizedName() {
        // Test for various USA abbreviations
        assertEquals("United States Of America", CountryToStandardizedCountry.standardize("US"));
        assertEquals("United States Of America", CountryToStandardizedCountry.standardize("USA"));
        assertEquals("United States Of America", CountryToStandardizedCountry.standardize("U.S."));
        assertEquals("United States Of America", CountryToStandardizedCountry.standardize("UNITED STATES"));
        assertEquals("United States Of America", CountryToStandardizedCountry.standardize("_UNITEDSTATES"));
        assertEquals("United State", CountryToStandardizedCountry.standardize("United State"));
    }

    @Test
    void standardize_whenCountryIsNotUsaVariant_returnsOriginalCountry() {
        // Test for non-USA countries
        assertEquals("Canada", CountryToStandardizedCountry.standardize("Canada"));
        assertEquals("Mexico", CountryToStandardizedCountry.standardize("Mexico"));
        assertEquals("Germany", CountryToStandardizedCountry.standardize("Germany"));
    }

    @Test
    void standardize_whenInputIsNull_throwsNullPointerException() {
        // Test for null input
        assertThrows(NullPointerException.class, () -> CountryToStandardizedCountry.standardize(null));
    }

    @Test
    void standardize_whenInputIsEmptyString_returnsEmptyString() {
        // Test for empty string input
        assertEquals("", CountryToStandardizedCountry.standardize(""));
    }
}