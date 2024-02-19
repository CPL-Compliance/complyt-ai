package com.complyt.business.transaction;

import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemsTotalCalculatorTest {

    @InjectMocks
    ItemsTotalCalculator itemsTotalCalculator;

    Transaction transaction;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void injectRecalculatedTotal_ItemsWithNoDiscount_CalculatedValueSameAsTotal() {
        // Given
        ShippingFee shippingFee = transaction.getShippingFee();
        Transaction transactionAfterCalculation = transaction
                .withItems(testUtilities.setCalculatedTotalOnItemList(transaction.getItems()))
                .withShippingFee(shippingFee.withCalculatedTotal(shippingFee.getTotalPrice()));


        // When
        Mono<Transaction> actualTransactionMono = itemsTotalCalculator.injectRecalculatedTotal(transaction);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotal_Items1WithDiscount_CalculatedValues() {
        // Given
        ShippingFee shippingFee = transaction.getShippingFee();
        List<Item> itemList = List.of(
                transaction.getItems().get(0),
                transaction.getItems().get(1)
                        .withDiscount(BigDecimal.valueOf(500))
        );
        Transaction transactionWithDiscount = transaction.withItems(itemList);

        Transaction transactionAfterCalculation = transactionWithDiscount
                .withItems(testUtilities.setCalculatedTotalOnItemList(transactionWithDiscount.getItems()))
                .withShippingFee(shippingFee.withCalculatedTotal(shippingFee.getTotalPrice()));


        // When
        Mono<Transaction> actualTransactionMono = itemsTotalCalculator.injectRecalculatedTotal(transactionWithDiscount);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotal_Items1WithDiscountShippingFeeNull_CalculatedValues() {
        // Given
        List<Item> itemList = List.of(
                transaction.getItems().get(0),
                transaction.getItems().get(1)
                        .withDiscount(BigDecimal.valueOf(500))
        );
        Transaction transactionWithDiscount = transaction.withItems(itemList)
                .withShippingFee(null);

        Transaction transactionAfterCalculation = transactionWithDiscount
                .withItems(testUtilities.setCalculatedTotalOnItemList(transactionWithDiscount.getItems()));


        // When
        Mono<Transaction> actualTransactionMono = itemsTotalCalculator.injectRecalculatedTotal(transactionWithDiscount);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotal_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                itemsTotalCalculator.injectRecalculatedTotal(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }
}