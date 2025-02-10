package io.complyt.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

public class CachedAddressDataTest {

    private CachedAddressData cachedAddressData;
    private CachedAddressData defaultAddressData;

    @BeforeEach
    void setUp() {
        cachedAddressData = TestUtilities.getCachedAddressData();
        defaultAddressData = new CachedAddressData(cachedAddressData.address(), cachedAddressData.scoring());
    }

    @Test
    void testDefaultCachedAddressData() {
        assertEquals(cachedAddressData.address(), defaultAddressData.address());
        assertEquals(defaultAddressData.scoring(), defaultAddressData.scoring());
    }

    @Test
    void testCachedAddressDataWithCustomValues() {
        Address customAddress = TestUtilities.getAddress().withCity("New York")
                .withCountry("USA")
                .withState("NY")
                .withZip("10001");
        Scoring customScoring = TestUtilities.getScoring().withScore(99.5);

        CachedAddressData customData = new CachedAddressData(customAddress, customScoring);

        assertEquals("New York", customData.address().city());
        assertEquals("USA", customData.address().country());
        assertEquals("NY", customData.address().state());
        assertEquals("10001", customData.address().zip());
        assertEquals(99.5, customData.scoring().score());
    }
}
