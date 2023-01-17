package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.v1.model.EconomicNexusTrackerDto;
import com.complyt.v1.model.PhysicalNexusTrackerDto;
import com.complyt.v1.model.SalesTaxTrackingDto;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SalesTaxTrackingMapperTest {

    private SalesTaxTracking salesTaxTracking;
    private SalesTaxTracking salesTaxTrackingNoTenantNorId;
    private SalesTaxTrackingDto salesTaxTrackingDto;
    private LocalDateTime localDateTime;
    private DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();
        domainObjectStub = new DomainObjectStub(new ComplytTimestamp(localDateTime), UUID.randomUUID().toString());
        salesTaxTracking = domainObjectStub.createSalesTaxTracking(UUID.randomUUID().toString());
        salesTaxTrackingNoTenantNorId = salesTaxTracking.withTenantId(null).withId(null).withComplytId(salesTaxTracking.getComplytId());
        salesTaxTrackingDto = domainObjectStub.createSalesTaxTrackingDto().withComplytId(salesTaxTracking.getComplytId());
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

}
