package com.complyt.v1.mappers;

import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.nexus.NexusCalculationSummaryDto;
import com.complyt.v1.models.nexus.TransactionNexusSummaryDto;
import com.complyt.v1.models.transaction.TransactionTypeDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class SalesTaxTrackingMapperTest {

    private SalesTaxTracking salesTaxTracking;
    private SalesTaxTracking salesTaxTrackingNoTenantNorId;
    private SalesTaxTrackingDto salesTaxTrackingDto;
    private LocalDateTime localDateTime;
    private UnitTestUtilities testUtilities;

    private UUID transactionId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString())
                .withNexusCalculationSummaries(Map.of(localDateTime.toLocalDate(), new NexusCalculationSummary(1, BigDecimal.valueOf(1200))))
                .withTransactionNexusSummaries(Map.of(transactionId, new TransactionNexusSummary(BigDecimal.valueOf(1200), localDateTime, TransactionType.INVOICE)));
        salesTaxTrackingNoTenantNorId = salesTaxTracking
                .withTenantId(null).withId(null).withComplytId(salesTaxTracking.getComplytId())
                .withClientTracking(salesTaxTracking.getClientTracking().withTenantId(null).withId(null))
                .withNexusStateRule(salesTaxTracking.getNexusStateRule().withId(null));
        salesTaxTrackingDto = testUtilities.createSalesTaxTrackingDto().withComplytId(salesTaxTracking.getComplytId())
                .withNexusCalculationSummaries(Map.of(localDateTime.toLocalDate(), new NexusCalculationSummaryDto(1, BigDecimal.valueOf(1200))))
                .withTransactionNexusSummaries(Map.of(transactionId, new TransactionNexusSummaryDto(BigDecimal.valueOf(1200), localDateTime, TransactionTypeDto.INVOICE)));
        ;
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
