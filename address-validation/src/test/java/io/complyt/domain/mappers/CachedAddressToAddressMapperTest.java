package io.complyt.domain.mappers;

import io.complyt.domain.Address;
import io.complyt.domain.AddressData;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.here.HereAddressData;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CachedAddressToAddressMapperTest {

    Address address = TestUtilities.getAddress();

    @Test
    void MapCachedAddressDataToAddress_CachedAddressData_ReturnsAddress() {
        // Given
        CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData();

        // When
        Address actualAddress = CachedAddressDataToAddressMapper.INSTANCE.map(cachedAddressData);

        // Then
        assertEquals(address, actualAddress);
    }

    @Test
    void map_CachedAddressDataSentAsAddressData_ReturnsAddress() {
        // Given
        CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData();

        // When
        Address actualAddress = CachedAddressDataToAddressMapper.INSTANCE.map(cachedAddressData);

        // Then
        assertEquals(address, actualAddress);
    }

    @Test
    void MapCachedAddressDataToAddress_Null_ReturnsNull() {
        // Given
        CachedAddressData cachedAddressData = null;

        // When
        Address actualAddress = CachedAddressDataToAddressMapper.INSTANCE.map(cachedAddressData);

        // Then
        assertEquals(null, actualAddress);
    }

    @Test
    void MapCachedAddressDataToAddress_NotCachedAddressData_ReturnsNull() {
        // Given
        CachedAddressData hereAddressData = null;

        // When
        Address actualAddress = CachedAddressDataToAddressMapper.INSTANCE.map(hereAddressData);

        // Then
        assertEquals(null, actualAddress);
    }

}