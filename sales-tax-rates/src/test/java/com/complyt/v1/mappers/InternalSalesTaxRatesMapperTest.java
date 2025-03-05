package com.complyt.v1.mappers;

import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import testUtils.TestUtilities;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class InternalSalesTaxRatesMapperTest {

    private final InternalSalesTaxRatesMapper mapper = Mappers.getMapper(InternalSalesTaxRatesMapper.class);

    InternalSalesTaxRates internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now());
    InternalSalesTaxRatesDto dto = TestUtilities.createInternalSalesTaxRatesDto()
            .withComplytId(internalSalesTaxRates.getComplytId());

    @Test
    void internalSalesTaxRatesDtoToInternalSalesTaxRates_validDto_returnsMappedObject() {
        // When
        InternalSalesTaxRates result = mapper.internalRatesDtoToInternalRates(dto);

        // Then
        assertNotNull(result);
        assertEquals(internalSalesTaxRates.getComplytId(), result.getComplytId());
    }


    @Test
    void internalSalesTaxRatesDtoToInternalSalesTaxRatesMapper_validDto_returnsMappedObject() {
        // When
        InternalSalesTaxRates result = InternalSalesTaxRatesMapper.INSTANCE.internalRatesDtoToInternalRates(dto);

        // Then
        assertNotNull(result);
        assertEquals(internalSalesTaxRates.getComplytId(), result.getComplytId());
    }

    @Test
    void internalSalesTaxRatesDtoToInternalSalesTaxRates_nullDto_returnsNull() {
        // When
        InternalSalesTaxRates result = mapper.internalRatesDtoToInternalRates(null);

        // Then
        assertNull(result);
    }
}
