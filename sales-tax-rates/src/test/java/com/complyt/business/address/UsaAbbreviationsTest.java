package com.complyt.business.address;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Map;

public class UsaAbbreviationsTest {

    @Test
    public void testUsaAbbreviationsList() {
        Map<String, String> abbreviations = UsaAbbreviations.usaAbbreviationsList;

        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("US"));
        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("U.S.A"));
        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("U.S"));
        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("U.S."));
        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("UNITED STATES"));
        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("UNITED STATES OF AMERICA"));
        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("_UNITEDSTATES"));
        Assertions.assertEquals(UsaAbbreviations.DEFAULT_COUNTRY, abbreviations.get("United State"));
    }
}