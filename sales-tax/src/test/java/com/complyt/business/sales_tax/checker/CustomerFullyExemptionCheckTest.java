package com.complyt.business.sales_tax.checker;

import com.complyt.domain.customer.exemption.ExemptionStatus;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionType;
import com.complyt.domain.customer.exemption.ValidationDates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class CustomerFullyExemptionCheckTest {

    CustomerFullyExemptionChecker customerFullyExemptionChecker;
    Transaction transaction;

    Exemption exemption;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        customerFullyExemptionChecker = new CustomerFullyExemptionChecker(transaction);
        exemption = testUtilities.createExemption(UUID.randomUUID().toString());
    }


    @Test
    void check_NullExemptionPassed_ThrowsException() {
        // Given
        Exemption nullExemption = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            customerFullyExemptionChecker.check(nullExemption);
        });

        assertEquals(nullPointerException.getMessage(), "exemption is marked non-null but is null");
    }

    @Test
    void check_TransactionFullyExempted_ReturnsTrue() {
        // Given
        Exemption expectedExemption = exemption;

        // When
        boolean isExempted = customerFullyExemptionChecker.check(expectedExemption);

        // Then
        assertTrue(isExempted);
    }

    @Test
    void check_TransactionIsNotFullyExempted_ReturnsFalse() {
        // Given
        Exemption expectedExemption = exemption.withExemptionType(ExemptionType.PARTIALLY);

        // When
        boolean isExempted = customerFullyExemptionChecker.check(expectedExemption);

        // Then
        assertFalse(isExempted);
    }

    @Test
    void check_TransactionIsBeforeTimeFrame_ReturnsFalse() {
        // Given
        Exemption expectedExemption = exemption.withValidationDates(new ValidationDates(
                LocalDateTime.now().plusYears(2),
                LocalDateTime.now().plusYears(3)));

        // When
        boolean isExempted = customerFullyExemptionChecker.check(expectedExemption);

        // Then
        assertFalse(isExempted);
    }

    @Test
    void check_TransactionIsAfterTimeFrame_ReturnsFalse() {
        // Given
        Exemption expectedExemption = exemption.withValidationDates(new ValidationDates(
                LocalDateTime.now().minusYears(2),
                LocalDateTime.now().minusYears(3)));

        // When
        boolean isExempted = customerFullyExemptionChecker.check(expectedExemption);

        // Then
        assertFalse(isExempted);
    }

    @Test
    void check_TransactionInTimeframeAndExemptionWithoutEndDate_ReturnsFalse() {
        // Given
        Exemption expectedExemption = exemption.withValidationDates(new ValidationDates(
                LocalDateTime.now().minusYears(2), null));

        // When
        boolean isExempted = customerFullyExemptionChecker.check(expectedExemption);

        // Then
        assertTrue(isExempted);
    }

    @Test
    void check_ExemptionStatusIsCANCELLED_ReturnsFalse() {
        // Given
        Exemption cancelledExemption = exemption.withExemptionStatus(ExemptionStatus.CANCELLED);

        // When
        boolean isExempted = customerFullyExemptionChecker.check(cancelledExemption);

        // Then
        assertFalse(isExempted);
    }

}
