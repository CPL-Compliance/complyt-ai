package com.complyt.business.tax.gt.gt_tax_web_client;

import com.complyt.business.tax.gt.*;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.tax.GtRates;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class TransactionGtRatesHandlerTest {
    TransactionGtRatesHandler transactionGtRatesHandler;
    Transaction transaction;
    UnitTestUtilities testUtilities;

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
        GtRatesProvider gtRatesProvider = new GtRatesProvider(new CountryLevelGtRatesCalculator(), new RegionLevelGtRatesCalculator());
        transactionGtRatesHandler = new TransactionGtRatesHandler(new ItemsGtRatesProvider(gtRatesProvider), new ShippingFeeGtRatesProvider(gtRatesProvider));
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createGtTransaction(UUID.randomUUID().toString())
                .withShippingFee(null);
    }

    @Test
    void setRates_SetsRatesToTransactionWithNoShippingFee_ReturnsModifiedTransaction() {
        // Given
        Transaction transaction = testUtilities.createTransaction(null).withShippingFee(null).withItems(testUtilities.createItems(false, true, true));
        GtRates gtRates = testUtilities.createGtRates();
        Item firstItemWithRates = transaction.getItems().get(0).withGtRates(gtRates);
        Item secondItemWithRates = transaction.getItems().get(1).withGtRates(gtRates);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(firstItemWithRates);
            add(secondItemWithRates);
        }};
        Transaction expectedTransaction = transaction.withItems(modifiedItems);

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        Mono<Transaction> actualTransaction = transactionGtRatesHandler.setRates(transaction, gtRates);

        // Then
        StepVerifier.create(actualTransaction).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void setRates_SetsRatesToTransactionWithNoShippingFeeAndNullRegion_ReturnsModifiedTransaction() {
        // Given
        Transaction transaction = testUtilities.createTransaction(null).withShippingFee(null).withItems(testUtilities.createItems(false, true, true));
        Transaction transactionToSend = transaction.withShippingAddress(transaction.getShippingAddress().withRegion(null));
        GtRates gtRates = testUtilities.createGtRates();
        Item firstItemWithRates = transaction.getItems().get(0).withGtRates(gtRates);
        Item secondItemWithRates = transaction.getItems().get(1).withGtRates(gtRates);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(firstItemWithRates);
            add(secondItemWithRates);
        }};
        Transaction expectedTransaction = transactionToSend.withItems(modifiedItems);

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        Mono<Transaction> actualTransaction = transactionGtRatesHandler.setRates(transactionToSend, gtRates);

        // Then
        StepVerifier.create(actualTransaction).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void setRates_SetsRatesToTransactionWithShippingFee_ReturnsModifiedTransaction() {
        // Given
        ShippingFee shippingFee = testUtilities.createShippingFee(false, true, true);
        List<Item> items = testUtilities.createItems(false, true, true);

        Transaction transaction = testUtilities.createTransaction(null).withShippingFee(shippingFee).withItems(items);

        GtRates gtRates = testUtilities.createGtRates();
        Item firstItemWithRates = transaction.getItems().get(0).withGtRates(gtRates);
        Item secondItemWithRates = transaction.getItems().get(1).withGtRates(gtRates);

        ShippingFee shippingFeeWithRates = shippingFee.withGtRates(gtRates);
        List<Item> modifiedItems = new ArrayList<>() {{
            add(firstItemWithRates);
            add(secondItemWithRates);
        }};

        Transaction expectedTransaction = transaction.withItems(modifiedItems).withShippingFee(shippingFeeWithRates);

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        Mono<Transaction> actualTransaction = transactionGtRatesHandler.setRates(transaction, gtRates);

        // Then
        StepVerifier.create(actualTransaction).expectNext(expectedTransaction).verifyComplete();
    }

    @Test
    void setRates_NullTransactionPassed_ThrowsException() {
        // Given
        GtRates gtRates = testUtilities.createGtRates();
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionGtRatesHandler.setRates(nullTransaction, gtRates));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void setRates_NullSalesTaxRatesPassed_ThrowsException() {
        // Given
        GtRates nullSalesTaxRate = null;
        Transaction transaction = testUtilities.createTransaction(null);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> transactionGtRatesHandler.setRates(transaction, nullSalesTaxRate));

        // Then
        assertEquals(nullPointerException.getMessage(), "gtRates is marked non-null but is null");
    }

}