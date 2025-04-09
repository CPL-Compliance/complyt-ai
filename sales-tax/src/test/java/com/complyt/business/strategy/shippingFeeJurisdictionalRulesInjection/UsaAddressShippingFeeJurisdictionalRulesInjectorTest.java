package com.complyt.business.strategy.shippingFeeJurisdictionalRulesInjection;

import com.complyt.business.strategy.ShippingFeeJurisdictionalRulesInjection.UsaAddressShippingFeeJurisdictionalRulesInjector;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.product_classification.ProductClassification;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;

public class UsaAddressShippingFeeJurisdictionalRulesInjectorTest {

    UsaAddressShippingFeeJurisdictionalRulesInjector usaAddressShippingFeeJurisdictionalRulesInjector;

    UnitTestUtilities testUtilities;

    Transaction transaction;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        usaAddressShippingFeeJurisdictionalRulesInjector = new UsaAddressShippingFeeJurisdictionalRulesInjector();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        ShippingAddress usaAddress = testUtilities.createShippingAddress();
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString())
                .withShippingAddress(usaAddress)
                .withShippingFee(testUtilities.createShippingFee(true, false, true));
    }

    @Test
    void inject_InjectsDataToTransaction_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));
        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules()
        );

        // When
        Transaction actualTransaction = usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(transaction, actualTransaction);
    }

    @Test
    void inject_InjectsDataToTransactionWithNotTaxableCategory_ReturnsModifiedTransaction() {
        // Given
        Transaction transactionNoRules = transaction.withShippingFee(transaction.getShippingFee().withJurisdictionalTaxRules(null));

        ShippingFee notTaxbleShippingFee = transaction.getShippingFee()
                .withTaxableCategory(TaxableCategory.NOT_TAXABLE)
                .withJurisdictionalSalesTaxRules(transaction.getShippingFee().getJurisdictionalSalesTaxRules().withTaxable(false));

        Transaction transactionWithNotTaxableShippingFee = transactionNoRules.withShippingFee(notTaxbleShippingFee);

        Map<String, ProductClassification> classifications = testUtilities.createUsaShippingFeeClassificationsMap(
                transaction.getShippingFee().getJurisdictionalSalesTaxRules().withTaxable(false)
        );

        // When
        Transaction actualTransaction = usaAddressShippingFeeJurisdictionalRulesInjector.inject(transactionNoRules).apply(classifications);

        // Then
        Assertions.assertEquals(transactionWithNotTaxableShippingFee, actualTransaction);
    }

}
