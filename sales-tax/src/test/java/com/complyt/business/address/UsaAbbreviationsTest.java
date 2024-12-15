package com.complyt.business.address;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class UsaAbbreviationsTest {

    @Test
    public void testUsaAbbreviationsList() {
        Map<String, String> abbreviations = UsaAbbreviations.usaAbbreviationsList;

        Assertions.assertEquals("USA", abbreviations.get("US"));
        Assertions.assertEquals("USA", abbreviations.get("USA"));
        Assertions.assertEquals("USA", abbreviations.get("U.S.A"));
        Assertions.assertEquals("USA", abbreviations.get("U.S"));
        Assertions.assertEquals("USA", abbreviations.get("U.S."));
        Assertions.assertEquals("USA", abbreviations.get("UNITED STATES"));
        Assertions.assertEquals("USA", abbreviations.get("UNITED STATES OF AMERICA"));
        Assertions.assertEquals("USA", abbreviations.get("_UNITEDSTATES"));
        Assertions.assertEquals("USA", abbreviations.get("United State"));
    }
}