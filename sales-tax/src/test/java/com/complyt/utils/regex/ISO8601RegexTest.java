package com.complyt.utils.regex;

import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ISO8601RegexTest {
    Pattern pattern = Pattern.compile(ISO8601Regex.expression);

    // === Date Tests ===
    @Test
    void testRegex_leapYearIn19s_ReturnsTrue() {
        // Given
        String date = "1976-02-29";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_leapYearIn20s_ReturnsTrue() {
        // Given
        String date = "2080-02-29";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_before1970_ReturnsTrue() {
        // Given
        String date = "1961-02-18";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_before1970InLeapYear_ReturnsTrue() {
        // Given
        String date = "1964-02-29";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_after2100InLeapYear_ReturnsTrue() {
        // Given
        String date = "2104-02-29";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_after2100_ReturnsTrue() {
        // Given
        String date = "2125-11-04";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_29thInFebruaryNotInLeapYearInThe19s_ReturnsTrue() {
        // Given
        String date = "1970-02-29";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_29thInFebruaryNotInLeapYearInThe20s_ReturnsTrue() {
        // Given
        String date = "2050-02-29";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_31thInWrongMonthNotInThe20s_ReturnsTrue() {
        // Given
        String date = "2007-04-31";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_31thInPossibleMonthInThe20s_ReturnsTrue() {
        // Given
        String date = "2000-07-31";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_MonthOver12In19s_ReturnsTrue() {
        // Given
        String date = "1999-13-03";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_MonthOver12In20s_ReturnsTrue() {
        // Given
        String date = "2054-13-19";

        // When + Then
        assertFalse(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    // === Time Tests ===
    @Test
    void testRegex_RoundHour_ReturnsTrue() {
        // Given
        String date = "2012-01-30T08:00:00";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_WithDigitsAfterTheDot_ReturnsTrue() {
        // Given
        String date = "2012-01-30T01:45:11.915935";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_WithDigitsAfterTheDotAndANegativeOffset_ReturnsTrue() {
        // Given
        String date = "2029-06-01T01:45:59.915935-18:00";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_WithDigitsAfterTheDotAndAPositiveOffset_ReturnsTrue() {
        // Given
        String date = "2020-03-27T03:13:22+17:59";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }

    @Test
    void testRegex_TimeWithNoSeconds_ReturnsTrue() {
        // Given
        String date = "2099-12-30T23:38";

        // When + Then
        assertTrue(UnitTestUtilities.stringPassedRegex(date, pattern));
    }
}