package com.complyt.v1.mappers;

import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.v1.models.SalesTaxTrackingDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SalesTaxTrackingMapperTest {

    private SalesTaxTracking salesTaxTracking;
    private SalesTaxTracking salesTaxTrackingNoTenantNorId;
    private SalesTaxTrackingDto salesTaxTrackingDto;
    private LocalDateTime localDateTime;
    private ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();
        objectStub = new ObjectStub(localDateTime, UUID.randomUUID().toString());
        salesTaxTracking = objectStub.createSalesTaxTracking(UUID.randomUUID().toString());
        salesTaxTrackingNoTenantNorId = salesTaxTracking.withTenantId(null).withId(null).withComplytId(salesTaxTracking.getComplytId());
        salesTaxTrackingDto = objectStub.createSalesTaxTrackingDto().withComplytId(salesTaxTracking.getComplytId());
    }

    @Test
    void salesTaxTrackingDtoToSalesTaxTracking_SalesTaxTrackingDto_ReturnsSalesTaxTracking() {
        // Given + When
        SalesTaxTrackingDto salesTaxTrackingDtoResult = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(salesTaxTracking);

        // Then
        assertEquals(salesTaxTrackingDto, salesTaxTrackingDtoResult);
    }

    @Test
    void salesTaxTrackingToSalesTaxTrackingDto_SalesTaxTracking_ReturnsSalesTaxTrackingDto() {
        // Given + When
        SalesTaxTracking salesTaxTrackingResult = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(salesTaxTrackingDto);

        // Then
        assertEquals(salesTaxTrackingNoTenantNorId, salesTaxTrackingResult);
    }

    @Test
    void mapping_nullSalesTaxTracking_ReturnNull() {
        // Given + When
        SalesTaxTracking givenSalesTaxTracking = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingDtoToSalesTaxTracking(null);
        SalesTaxTrackingDto givenSalesTaxTrackingDto = SalesTaxTrackingMapper.INSTANCE.salesTaxTrackingToSalesTaxTrackingDto(null);

        // Then
        assertNull(givenSalesTaxTracking);
        assertNull(givenSalesTaxTrackingDto);
    }

}
