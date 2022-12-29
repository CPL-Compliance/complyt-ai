package com.complyt.v1.mappers;

import com.complyt.domain.State;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.v1.model.EconomicNexusTrackerDto;
import com.complyt.v1.model.PhysicalNexusTrackerDto;
import com.complyt.v1.model.SalesTaxTrackingDto;
import com.complyt.v1.model.StateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SalesTaxTrackingMapperTest {

    private SalesTaxTracking salesTaxTracking;
    private SalesTaxTrackingDto salesTaxTrackingDto;
    private LocalDateTime localDateTime;

    String salesTaxTrackingId;

    @BeforeEach
    void setUp() {
        salesTaxTracking = createSalesTaxTracking();
        salesTaxTrackingDto = createSalesTaxTrackingDto();
        localDateTime = LocalDateTime.now();
        salesTaxTrackingId = UUID.randomUUID().toString();
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        PhysicalNexusTracker physicalNexusTracker = new PhysicalNexusTracker(false, null);
        EconomicNexusTracker economicNexusTracker = new EconomicNexusTracker(true, localDateTime);
        return new SalesTaxTracking(salesTaxTrackingId, state, null,
                true, physicalNexusTracker, economicNexusTracker, null, true, localDateTime);
    }

    private SalesTaxTrackingDto createSalesTaxTrackingDto() {
        StateDto state = new StateDto("CA", "02", "California");
        PhysicalNexusTrackerDto physicalNexusTracker = new PhysicalNexusTrackerDto(false, null);
        EconomicNexusTrackerDto economicNexusTracker = new EconomicNexusTrackerDto(true, localDateTime);
        return new SalesTaxTrackingDto(salesTaxTrackingId, state, true, physicalNexusTracker, economicNexusTracker, null, true, localDateTime);
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
        assertEquals(salesTaxTracking, salesTaxTrackingResult);
    }

}
