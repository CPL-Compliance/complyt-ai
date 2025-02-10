package io.complyt.v1.mappers;

import io.complyt.domain.CachedAddressData;
import io.complyt.v1.models.CachedAddressDataDto;
import org.junit.jupiter.api.Test;
import test_utils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CachedAddressDataMapperTest {

    private CachedAddressData cachedAddressData = TestUtilities.getCachedAddressData();
    private final CachedAddressDataDto cachedAddressDataDto = TestUtilities.getCachedAddressDataDto();

    @Test
    void cachedAddressDataToCachedAddressDataDto_ValidCachedAddressData_ReturnsDto() {
        // When
        cachedAddressData = cachedAddressData.withAddress(cachedAddressData.address().withIsPartial(true));
        CachedAddressDataDto result = CachedAddressDataMapper.INSTANCE.cachedAddressDataToCachedAddressDataDto(cachedAddressData);

        // Then
        assertEquals(cachedAddressDataDto, result);
    }

    @Test
    void cachedAddressDataToCachedAddressDataDto_NullCachedAddressData_ReturnsNull() {
        // When
        CachedAddressDataDto result = CachedAddressDataMapper.INSTANCE.cachedAddressDataToCachedAddressDataDto(null);

        // Then
        assertNull(result);
    }
}