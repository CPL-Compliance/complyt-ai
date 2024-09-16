package com.complyt.business.timestamps_injection.provider;

import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class NexusAppliedDateProviderTest {

    @InjectMocks
    private NexusAppliedDateProvider nexusAppliedDateProvider;

    @Mock
    private SalesTaxTracking salesTaxTracking;

    private LocalDateTime updatedAppliedDate;
    private LocalDateTime defaultAppliedDate;

    @BeforeEach
    void setUp() {
        updatedAppliedDate = LocalDateTime.now();
        defaultAppliedDate = EconomicNexusTracker.DEFAULT_ESTABLISHED_DATE;
    }

    @Test
    void getAppliedDate_whenAppliedDateIsDefault_shouldReturnUpdatedAppliedDate() {
        // Given
        when(salesTaxTracking.getAppliedDate()).thenReturn(defaultAppliedDate);

        // When
        LocalDateTime result = nexusAppliedDateProvider.getAppliedDate(salesTaxTracking, updatedAppliedDate);

        // Then
        assertEquals(updatedAppliedDate, result, "Applied date should be updated when it's the default.");
    }

    @Test
    void getAppliedDate_whenAppliedDateIsBeforeUpdated_shouldReturnAppliedDate() {
        // Given
        LocalDateTime appliedDate = updatedAppliedDate.minusDays(10);
        when(salesTaxTracking.getAppliedDate()).thenReturn(appliedDate);

        // When
        LocalDateTime result = nexusAppliedDateProvider.getAppliedDate(salesTaxTracking, updatedAppliedDate);

        // Then
        assertEquals(appliedDate, result, "Applied date should not be updated if it's before the updated date.");
    }

    @Test
    void getAppliedDate_whenAppliedDateIsAfterUpdated_shouldReturnUpdatedAppliedDate() {
        // Given
        LocalDateTime appliedDate = updatedAppliedDate.plusDays(10);
        when(salesTaxTracking.getAppliedDate()).thenReturn(appliedDate);

        // When
        LocalDateTime result = nexusAppliedDateProvider.getAppliedDate(salesTaxTracking, updatedAppliedDate);

        // Then
        assertEquals(updatedAppliedDate, result, "Applied date should be updated when it's after the updated date.");
    }
}