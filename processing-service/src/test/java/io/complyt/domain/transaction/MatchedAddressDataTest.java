package io.complyt.domain.transaction;

import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.enums.FieldMatchType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchedAddressDataTest {

    @Test
    void testConstructorAndGetters() {
        MandatoryAddress address = new MandatoryAddress("City", "Country", "County", "State", "Street", "Region", "12345", false);
        FieldsMatchScore fieldScore = new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.GOOD, FieldMatchType.GOOD, FieldMatchType.PARTIAL, FieldMatchType.GOOD, FieldMatchType.EXACT);
        Scoring scoring = new Scoring(MatchLevelType.EXCELLENT, 0.98, fieldScore);

        MatchedAddressData matched = new MatchedAddressData(address, scoring);

        assertEquals(address, matched.address());
        assertEquals(scoring, matched.scoring());
    }

    @Test
    void testWithMethodCreatesModifiedCopy() {
        MandatoryAddress address1 = new MandatoryAddress("City", "Country", "County", "State", "Street", "Region", "12345", false);
        MandatoryAddress address2 = new MandatoryAddress("NewCity", "Country", "County", "State", "Street", "Region", "12345", false);

        Scoring scoring = new Scoring(MatchLevelType.EXCELLENT, 0.98,
                new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT,
                        FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));

        MatchedAddressData original = new MatchedAddressData(address1, scoring);
        MatchedAddressData modified = original.withAddress(address2);

        assertNotEquals(original, modified);
        assertEquals(address2, modified.address());
        assertEquals(scoring, modified.scoring());
    }

    @Test
    void testEqualsAndHashCode() {
        MandatoryAddress address = new MandatoryAddress("City", "Country", "County", "State", "Street", "Region", "12345", false);
        Scoring scoring = new Scoring(MatchLevelType.GOOD, 0.7,
                new FieldsMatchScore(FieldMatchType.GOOD, FieldMatchType.GOOD, FieldMatchType.GOOD,
                        FieldMatchType.GOOD, FieldMatchType.GOOD, FieldMatchType.GOOD));

        MatchedAddressData data1 = new MatchedAddressData(address, scoring);
        MatchedAddressData data2 = new MatchedAddressData(address, scoring);

        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
    }

    @Test
    void testToStringContainsFieldValues() {
        MandatoryAddress address = new MandatoryAddress("City", "Country", "County", "State", "Street", "Region", "12345", false);
        Scoring scoring = new Scoring(MatchLevelType.FAIR, 0.6,
                new FieldsMatchScore(FieldMatchType.PARTIAL, FieldMatchType.GOOD, FieldMatchType.EXACT,
                        FieldMatchType.PARTIAL, FieldMatchType.GOOD, FieldMatchType.NO_MATCH));

        MatchedAddressData data = new MatchedAddressData(address, scoring);

        String str = data.toString();
        assertTrue(str.contains("City"));
        assertTrue(str.contains("0.6"));
    }
}
