package com.complyt.business.sales_tax.checker;

import com.complyt.domain.*;
import com.complyt.domain.nexus.EconomicNexusTracker;
import com.complyt.domain.nexus.PhysicalNexusTracker;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SalesTaxApplyCheckTest {

    private SalesTaxApplyCheck salesTaxApplyCheck;
    private SalesTaxTracking salesTaxTracking;

    @Mock
    private CustomerFullyExemptionCheck customerFullyExemptionCheck;

    @BeforeEach
    void setUp() {
        salesTaxTracking = createSalesTaxTracking();
    }

    private SalesTaxTracking createSalesTaxTracking() {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID().toString(), state,
                UUID.randomUUID().toString(), true,
                new PhysicalNexusTracker(false, null),
                new EconomicNexusTracker(false, null), LocalDateTime.now(),
                true, LocalDateTime.now());
    }


    private Transaction createTransactionWithAppliedReferenceDate() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };

        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, null, new TimeStamps(salesTaxTracking.getAppliedDate().plusYears(1), salesTaxTracking.getAppliedDate().plusYears(1)), TransactionType.INVOICE, null);
    }

    private Transaction createTransactionWithReferenceDateNotApplied() {
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };

        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, null, new TimeStamps(salesTaxTracking.getAppliedDate().minusYears(1), salesTaxTracking.getAppliedDate().minusYears(1)), TransactionType.INVOICE, null);
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
                .withApprovalDate(transaction.getExternalTimeStamps().getCreatedDate().plusYears(1));

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
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxApplyCheck = new SalesTaxApplyCheck(nullTransaction);
        });

        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void isApplied_NullTrackingPassed_ThrowsException() {
        // Given
        Transaction transaction = createTransactionWithAppliedReferenceDate();
        salesTaxApplyCheck = new SalesTaxApplyCheck(transaction);
        SalesTaxTracking nullTracking = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            salesTaxApplyCheck.check(nullTracking);
        });

        assertEquals(nullPointerException.getMessage(), "salesTaxTracking is marked non-null but is null");
    }

}
