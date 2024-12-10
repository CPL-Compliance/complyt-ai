package com.complyt.v1.mappers;

import com.complyt.domain.internal_rates.InternalAddress;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalAddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.*;

class InternalAddressMapperTest {
    private InternalAddressDto internalAddressDto;
    private InternalAddress internalAddress;

    @BeforeEach
    void setUp() {
        internalAddressDto = TestUtilities.createInternalAddressDto("California", "Fresno", "Fresno");
        internalAddress = TestUtilities.createInternalAddress();
    }


    @Test
    void internalAddressDtoToInternalAddress_validDto_returnsInternalAddress() {
        // Given
        InternalAddressDto dto = internalAddressDto;

        // When
        InternalAddress result = InternalAddressMapper.INSTANCE.internalAddressDtoToInternalAddress(dto);

        // Then
        assertEquals(internalAddress, result);
    }

    @Test
    void internalAddressDtoToInternalAddress_nullDto_returnsNull() {
        // When
        InternalAddress result = InternalAddressMapper.INSTANCE.internalAddressDtoToInternalAddress(null);

        // Then
        assertNull(result);
    }

    @Test
    void internalSalesTaxRatesToInternalSalesTaxRatesDto_validInternalAddress_returnsInternalAddressDto() {
        // Given
        InternalAddress address = internalAddress;

        // When
        InternalAddressDto result = InternalAddressMapper.INSTANCE.internalSalesTaxRatesToInternalSalesTaxRatesDto(address);

        // Then
        assertEquals(result, internalAddressDto);
    }

    @Test
    void internalSalesTaxRatesToInternalSalesTaxRatesDto_nullInternalAddress_returnsNull() {
        // When
        InternalAddressDto result = InternalAddressMapper.INSTANCE.internalSalesTaxRatesToInternalSalesTaxRatesDto(null);

        // Then
        assertNull(result);
    }
}