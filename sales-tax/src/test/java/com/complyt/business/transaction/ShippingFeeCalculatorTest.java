package com.complyt.business.transaction;

import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ShippingFeeCalculatorTest {

    @InjectMocks
    ShippingFeeCalculator shippingFeeCalculator;

    Transaction transaction;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_transactionWithShippingFee_returnTransaction() {
        // Given
        ShippingFee shippingFee = transaction.getShippingFee();
        Transaction transactionAfterCalculation = transaction
                .withShippingFee(shippingFee.withCalculatedTotal(shippingFee.getTotalPrice()));


        // When
        Mono<Transaction> actualTransactionMono = shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(transaction);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_transactionWithoutShippingFee_returnTransaction() {
        // Given
        transaction = transaction.withShippingFee(null);
        Transaction transactionAfterCalculation = transaction;

        // When
        Mono<Transaction> actualTransactionMono = shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(transaction);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                shippingFeeCalculator.injectRecalculatedTotalAfterDiscount(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }
}