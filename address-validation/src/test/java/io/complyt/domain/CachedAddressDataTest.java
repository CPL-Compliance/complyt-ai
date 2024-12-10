package io.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import test_utils.TestUtilities;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CachedAddressDataTest {
    private CachedAddressData cachedAddressData;
    private CachedAddressData defaultAddressData;

    @BeforeEach
    void setUp() {
        cachedAddressData = TestUtilities.getCachedAddressData();
        defaultAddressData = CachedAddressData.DEFAULT;
    }

    @Test
    void testDefaultCachedAddressData() {
        assertEquals("UNKNOWN", defaultAddressData.city());
        assertEquals("UNKNOWN", defaultAddressData.country());
        assertEquals("UNKNOWN", defaultAddressData.county());
        assertEquals("UNKNOWN", defaultAddressData.state());
        assertEquals("UNKNOWN", defaultAddressData.street());
        assertEquals("UNKNOWN", defaultAddressData.zip());
        assertFalse(defaultAddressData.isPartial());
        assertEquals(0.0, defaultAddressData.score());
    }

    @Test
    void testCachedAddressDataWithCustomValues() {
        CachedAddressData customData = cachedAddressData.withCity("New York")
                .withCountry("USA")
                .withState("NY")
                .withZip("10001")
                .withScore(99.5);

        assertEquals("New York", customData.city());
        assertEquals("USA", customData.country());
        assertEquals("NY", customData.state());
        assertEquals("10001", customData.zip());
        assertEquals(99.5, customData.score());
    }}