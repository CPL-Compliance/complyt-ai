package com.complyt.domain.mappers;

import com.complyt.domain.common_rates.CommonRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.internal_rates.InternalSalesTaxRates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.TestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
class InternalRatesToCommonRatesMapperTest {

    InternalSalesTaxRates internalSalesTaxRates;

    @BeforeEach
    void setup() {
        internalSalesTaxRates = TestUtilities.createInternalSalesTaxRates(LocalDateTime.now(), UUID.randomUUID());
    }

    @Test
    void map_nullSalesTaxData_ReturnNull() {
        // Given
        CommonSalesTaxRates expectedCommonSalesTaxRates = TestUtilities.createExternalCommonSalesTaxRates()
                .withSalesTaxRates(TestUtilities.createCommonRates())
                .withAddress(TestUtilities.createCommonAddressFromAddress(internalSalesTaxRates.getAddress()))
                .withComplytId(internalSalesTaxRates.getComplytId());

        // Given+ When
        CommonSalesTaxRates commonSalesTaxRates = InternalRatesToCommonRatesMapper.INSTANCE.map(internalSalesTaxRates);

        // Then
        assertEquals(expectedCommonSalesTaxRates, commonSalesTaxRates);
    }

    @Test
    void map_nullInternalSalesTaxRates_ReturnNull() {
        // Given+ When
        CommonSalesTaxRates commonSalesTaxRates = InternalRatesToCommonRatesMapper.INSTANCE.map(null);

        // Then
        assertNull(commonSalesTaxRates);
    }

    @Test
    void mapSalesTaxRatesWithOtherSum_nullInternalSalesTaxRates_ReturnNull() {
        CommonRates rate = InternalRatesToCommonRatesMapper.INSTANCE.mapSalesTaxRatesWithOtherSum(null);
        assertNull(rate);
    }

    @Test
    void sumNonNull_nullInternalSalesTaxRates_ReturnNull() {
        BigDecimal total = InternalRatesToCommonRatesMapper.INSTANCE.sumNonNull(null, BigDecimal.ONE);
        assertEquals(BigDecimal.ONE, total);
    }

}