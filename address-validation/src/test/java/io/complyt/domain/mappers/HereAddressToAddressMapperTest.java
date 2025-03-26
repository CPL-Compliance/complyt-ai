package io.complyt.domain.mappers;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.here.HereAddress;
import io.complyt.domain.here.HereAddressData;
import io.complyt.domain.here.HereAddressItem;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HereAddressToAddressMapperTest {

    private final CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData().withScoring(TestUtilities.getScoring());

    @Test
    void mapHereAddressDataToAddress_HereAddressData_ReturnsMappedAddresses() {
        // Given
        HereAddressData hereAddressData = TestUtilities.getHereAddressData();

        // When
        List<CachedAddressData> actualAddresses = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(1, actualAddresses.size());
        assertEquals(cachedAddressData.address(), actualAddresses.get(0).address());
        assertEquals(cachedAddressData.scoring(), actualAddresses.get(0).scoring());

    }

    @Test
    void mapHereAddressDataToAddress_Null_ReturnsDefault() {
        // Given
        HereAddressData hereAddressData = new HereAddressData(null);

        // When
        List<CachedAddressData> actualAddresses = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertTrue(actualAddresses.isEmpty());
    }

    @Test
    void mapHereAddressDataToAddress_Empty_ReturnsDefault() {
        // Given
        HereAddressData hereAddressData = TestUtilities.getHereAddressData().withItems(List.of());

        // When
        List<CachedAddressData> actualAddresses = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertTrue(actualAddresses.isEmpty());
    }

    @Test
    void mapHereAddressDataToAddress_NullList_ReturnsDefault() {
        // Given
        HereAddressData hereAddressData = TestUtilities.getHereAddressData().withItems(null);

        // When
        List<CachedAddressData> actualAddresses = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertTrue(actualAddresses.isEmpty());
    }

    @Test
    void mapHereAddressDataToAddress_NullItem_ReturnsDefault() {
        // Given
        ArrayList<HereAddressItem> list = new ArrayList<>();
        list.add(null);
        HereAddressData hereAddressData = TestUtilities.getHereAddressData().withItems(list);

        // When
        List<CachedAddressData> actualAddresses = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertTrue(actualAddresses.isEmpty());
    }

    @Test
    void mapHereAddressDataToAddress_HereAddressDataNonUSA_ReturnsMappedAddresses() {
        // Given
        HereAddress hereAddress = new HereAddress("", "CA", "Canada", null, "Quebec", "County", null,  null, null);
        HereAddressItem hereAddressItem = TestUtilities.getHereAddressItem().withAddress(hereAddress);
        HereAddressData hereAddressData = TestUtilities.getHereAddressData().withItems(List.of(hereAddressItem));

        // When
        List<CachedAddressData> actualAddresses = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(1, actualAddresses.size());
        // State, Region address Tests
        assertNull(actualAddresses.get(0).address().state());
        assertNotNull(actualAddresses.get(0).address().region());
        // State, Region scoring Tests
        assertNull(actualAddresses.get(0).scoring().fieldScore().stateMatch());
        assertNotNull(actualAddresses.get(0).scoring().fieldScore().regionMatch());

        assertEquals(hereAddress.state(), actualAddresses.get(0).address().region()); // Region == State in Here
        assertEquals(FieldMatchType.fromScore(hereAddressItem.scoring().fieldScore().state()), actualAddresses.get(0).scoring().fieldScore().regionMatch());

    }
}
