package io.complyt.domain.mappers;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.here.HereAddressData;
import io.complyt.domain.here.HereAddressItem;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HereAddressToAddressMapperTest {

    CachedAddressData address = TestUtilities.getCachedAddressData();
    CachedAddressData addressDefault = CachedAddressData.DEFAULT;

    @Test
    void MapHereAddressDataToAddress_HereAddressData_ReturnsAddress() {
        // Given
        HereAddressData hereAddressData = TestUtilities.getHereAddressData();

        // When
        CachedAddressData actualAddress = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(address, actualAddress);
    }

    @Test
    void MapHereAddressDataToAddress_Null_ReturnsDefault() {
        // Given
        HereAddressData hereAddressData = new HereAddressData(null);

        // When
        CachedAddressData actualAddress = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(addressDefault, actualAddress);
    }

    @Test
    void MapHereAddressDataToAddress_Empty_ReturnsDefault() {
        // Given
        HereAddressData hereAddressData = TestUtilities.getHereAddressData().withItems(List.of());

        // When
        CachedAddressData actualAddress = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(addressDefault, actualAddress);
    }

    @Test
    void MapHereAddressDataToAddress_NullList_ReturnsDefault() {
        // Given
        HereAddressData hereAddressData = TestUtilities.getHereAddressData().withItems(null);

        // When
        CachedAddressData actualAddress = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(addressDefault, actualAddress);
    }

    @Test
    void MapHereAddressDataToAddress_NullItem_ReturnsDefault() {
        // Given
        ArrayList<HereAddressItem> list = new ArrayList<>();
        list.add(null);
        HereAddressData hereAddressData = TestUtilities.getHereAddressData().withItems(list);

        // When
        CachedAddressData actualAddress = HereAddressToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(addressDefault, actualAddress);
    }
}