package com.complyt.business.transaction.data_checker;
import com.complyt.business.tax.sales_tax.checker.SalesTaxApplyCheck;
import com.complyt.domain.decorator.SalesTaxTrackingWithNexusInfo;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AddressValidationApplyCheckerTest {

    private AddressValidationApplyChecker checker;
    private Transaction transaction;
    private SalesTaxTrackingWithNexusInfo salesTaxTrackingWithNexusInfo;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        checker = new AddressValidationApplyChecker();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(testUtilities.createSalesTaxTracking(UUID.randomUUID().toString()), true);

    }

    @Test
    void shouldValidateAddress_CountryIsUSA_HasNexusAndApplied_ReturnsFalse() {
        boolean result = checker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldValidateAddress_CountryIsUSA_NoNexus_ReturnsTrue() {
        // Given
        salesTaxTrackingWithNexusInfo = new SalesTaxTrackingWithNexusInfo(salesTaxTrackingWithNexusInfo.getSalesTaxTracking(), false);

        // When
        boolean result = checker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldValidateAddress_CountryIsUSA_NotApplied_ReturnsTrue() {
        // Given
        transaction = transaction.withTransactionType(TransactionType.REFUND);
        // When
        boolean result = checker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo);

        // Then
        assertTrue(result);
    }

    @Test
    void shouldValidateAddress_CountryIsNotUSA_ReturnsFalse() {
        // Given
        transaction = transaction.withShippingAddress(transaction.getShippingAddress().withCountry("Israel"));

        // When
        boolean result = checker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo);

        // Then
        assertFalse(result);
    }

    @Test
    void shouldValidateAddress_NullTransaction_ThrowsNullPointerException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            checker.shouldValidateAddress(nullTransaction, salesTaxTrackingWithNexusInfo);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void inject_NullSalesTaxTrackingWithInfoNull_ThrowsNullPointerException() {
        salesTaxTrackingWithNexusInfo = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            checker.shouldValidateAddress(transaction, salesTaxTrackingWithNexusInfo);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "salesTaxTrackingWithNexusInfo is marked non-null but is null");
    }
}