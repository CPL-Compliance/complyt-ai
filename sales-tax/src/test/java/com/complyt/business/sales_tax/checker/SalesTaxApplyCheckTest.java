package com.complyt.business.sales_tax.checker;

import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionType;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxApplyCheckTest {

    private SalesTaxApplyCheck salesTaxApplyCheck;
    private SalesTaxTracking salesTaxTracking;

    TestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        salesTaxTracking = testUtilities.createSalesTaxTracking(UUID.randomUUID().toString());
    }


    private Transaction createTransactionWithAppliedReferenceDate() {
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(salesTaxTracking.getAppliedDate().plusYears(1));
        Timestamps externalTimestamps = new Timestamps(complytTimestamp, complytTimestamp);
        return testUtilities.createTransaction(UUID.randomUUID().toString()).withExternalTimestamps(externalTimestamps);
    }

    private Transaction createTransactionWithReferenceDateNotApplied() {
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(salesTaxTracking.getAppliedDate().minusYears(1));
        Timestamps externalTimestamps = new Timestamps(complytTimestamp, complytTimestamp);
        return testUtilities.createTransaction(UUID.randomUUID().toString()).withExternalTimestamps(externalTimestamps);
    }

    @Test
    void isApplied_SalesTaxApplied_ReturnsTrue() {
        // Given
        Transaction transaction = createTransactionWithAppliedReferenceDate();
        salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);

        // When + Then
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTracking);

        assertTrue(isApplied);
    }

    @Test
    void isApplied_SalesTaxNotAppliedBecauseOfApplicationDate_ReturnsFalse() {
        // Given
        Transaction transaction = createTransactionWithReferenceDateNotApplied();
        salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);

        // When + Then
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTracking);

        assertFalse(isApplied);
    }

    @Test
    void isApplied_SalesTaxNotAppliedBecauseEnforcedSalesTaxIsFalse_ReturnsFalse() {
        // Given
        SalesTaxTracking salesTaxTrackingWithNoSalesTax = salesTaxTracking.withEnforcesSalesTax(false);
        Transaction transaction = createTransactionWithAppliedReferenceDate();
        salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);

        // When + Then
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTrackingWithNoSalesTax);

        assertFalse(isApplied);
    }

    @Test
    void isApplied_SalesTaxNotAppliedBecauseNotApproved_ReturnsFalse() {
        // Given
        Transaction transaction = createTransactionWithAppliedReferenceDate();
        salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);

        SalesTaxTracking salesTaxTrackingWithNoSalesTax = salesTaxTracking
                .withApproved(false)
                .withApprovalDate(transaction.getExternalTimestamps().getCreatedDate().getTimestamp().plusYears(1));

        // When
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTrackingWithNoSalesTax);

        // Then
        assertFalse(isApplied);
    }

    @Test
    void isApplied_SalesTaxNotAppliedBecauseTransactionIsOfTypeRefund_ReturnsFalse() {
        // Given
        Transaction transaction = createTransactionWithAppliedReferenceDate()
                .withTransactionType(TransactionType.REFUND);
        salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);

        SalesTaxTracking salesTaxTrackingWithNoSalesTax = salesTaxTracking
                .withApproved(true);

        // When
        boolean isApplied = salesTaxApplyCheck.check(salesTaxTrackingWithNoSalesTax);

        // Then
        assertFalse(isApplied);
    }

    @Test
    void isApplied_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxApplyCheck = new SalesTaxApplyCheck(nullTransaction));

        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void isApplied_NullTrackingPassed_ThrowsException() {
        // Given
        Transaction transaction = createTransactionWithAppliedReferenceDate();
        salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);
        SalesTaxTracking nullTracking = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> salesTaxApplyCheck.check(nullTracking));

        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

}
