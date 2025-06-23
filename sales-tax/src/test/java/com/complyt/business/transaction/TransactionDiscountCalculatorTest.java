package com.complyt.business.transaction;

import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import com.complyt.v1.exceptions.types.InvalidDiscountAmountException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.BaseTestClass;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionDiscountCalculatorTest extends BaseTestClass  {

    @InjectMocks
    TransactionDiscountCalculator transactionDiscountCalculator;

    Transaction transaction;

    UnitTestUtilities testUtilities;



    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransactionWithCalculatedTotalItem(UUID.randomUUID().toString());
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_TransactionWithNoDiscount_returnsTransaction() {
        // Given
        Transaction transactionWithDiscount = transaction.withTransactionLevelDiscount(BigDecimal.valueOf(950));

        BigDecimal transactionTotalPrice = transactionWithDiscount.getItems().stream().map(Item::getCalculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal relativeDiscountPercentageForNewAmountCalculation = new BigDecimal(transactionWithDiscount.getTransactionLevelDiscount().divide(transactionTotalPrice, 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()); // transaction discount is positive

        Transaction transactionAfterCalculation = transactionWithDiscount.withItems(testUtilities.setCalculatedTotalAndRelativeDiscountOnItemsList(transactionWithDiscount.getItems(), relativeDiscountPercentageForNewAmountCalculation));

        // When
        Mono<Transaction> actualTransactionMono = transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionWithDiscount);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_TransactionWith950Discount_returnsTransactionWithRelativeDiscountInItems() {
        // Given
        Transaction transactionWithDiscount = transaction.withTransactionLevelDiscount(BigDecimal.valueOf(950)); // 10% discount

        BigDecimal transactionTotalPrice = transactionWithDiscount.getItems().stream().map(Item::getCalculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal relativeDiscountPercentageForNewAmountCalculation = new BigDecimal(transactionWithDiscount.getTransactionLevelDiscount().divide(transactionTotalPrice, 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()); // transaction discount is positive

        Transaction transactionAfterCalculation = transactionWithDiscount.withItems(testUtilities.setCalculatedTotalAndRelativeDiscountOnItemsList(transactionWithDiscount.getItems(), relativeDiscountPercentageForNewAmountCalculation));

        // When
        Mono<Transaction> actualTransactionMono = transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionWithDiscount);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_TransactionWithDiscountAndTwoHighestAmountItem_returnsTransactionWithRelativeDiscountInItems() {
        // Given
        transaction = testUtilities.createTransactionWithThreeItemsAndCalculatedTotal(UUID.randomUUID().toString()); // transaction with 2 items with highest price
        Transaction transactionWithDiscount = transaction.withTransactionLevelDiscount(BigDecimal.valueOf(1750)); // 10% discount

        BigDecimal transactionTotalPrice = transactionWithDiscount.getItems().stream().map(Item::getCalculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal relativeDiscountPercentageForNewAmountCalculation = new BigDecimal(transactionWithDiscount.getTransactionLevelDiscount().divide(transactionTotalPrice, 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()); // transaction discount is positive

        Transaction transactionAfterCalculation = transactionWithDiscount.withItems(testUtilities.setCalculatedTotalAndRelativeDiscountOnItemsList(transactionWithDiscount.getItems(), relativeDiscountPercentageForNewAmountCalculation));

        // When
        Mono<Transaction> actualTransactionMono = transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionWithDiscount);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_TransactionWithNonRoundDiscount_returnsTransactionWithRelativeDiscountInItems() {
        // Given
        Transaction transactionWithDiscount = transaction.withTransactionLevelDiscount(BigDecimal.valueOf(100)); // random discount that is not round

        BigDecimal transactionTotalPrice = transactionWithDiscount.getItems().stream().map(Item::getCalculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal relativeDiscountPercentageForNewAmountCalculation = new BigDecimal(transactionWithDiscount.getTransactionLevelDiscount().divide(transactionTotalPrice, 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()); // transaction discount is positive

        Transaction transactionAfterTransactionDiscountCalculation = transactionWithDiscount.withItems(testUtilities.setCalculatedTotalAndRelativeDiscountOnItemsList(transactionWithDiscount.getItems(), relativeDiscountPercentageForNewAmountCalculation));

        // The remains is 0.003 dollars
        Transaction transactionAfterCalculation = transactionAfterTransactionDiscountCalculation.withItems(
                transactionAfterTransactionDiscountCalculation.getItems().stream().map(item -> item.getTotalPrice().equals(BigDecimal.valueOf(8000)) ?
                        item.withRelativeTransactionDiscount(item.getRelativeTransactionDiscount().add(BigDecimal.valueOf(0.003)))
                                .withCalculatedTotal(item.getCalculatedTotal().subtract(BigDecimal.valueOf(0.003))) : item).collect(Collectors.toList()));

        // When
        Mono<Transaction> actualTransactionMono = transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionWithDiscount);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_TransactionWithItemDiscount_returnsTransaction() {
        // Given
        Transaction transactionWithDiscount = transaction.withTransactionLevelDiscount(BigDecimal.valueOf(930));

        BigDecimal transactionTotalPrice = transactionWithDiscount.getItems().stream().map(Item::getCalculatedTotal).reduce(BigDecimal.ZERO, BigDecimal::add).subtract(BigDecimal.valueOf(200));
        BigDecimal relativeDiscountPercentageForNewAmountCalculation = new BigDecimal(transactionWithDiscount.getTransactionLevelDiscount().divide(transactionTotalPrice, 6, RoundingMode.HALF_UP).stripTrailingZeros().toPlainString()); // transaction discount is positive

        Transaction transactionBeforeItemsDiscount = transactionWithDiscount.withItems(transactionWithDiscount.getItems().stream().map(item -> item.withDiscount(BigDecimal.valueOf(100))).collect(Collectors.toList()));
        Transaction transactionAfterItemsDiscountAndBeforeTransactionDiscount = transactionBeforeItemsDiscount.withItems(testUtilities.setCalculatedTotalOnItemList(transactionBeforeItemsDiscount.getItems()));
        Transaction transactionAfterAllDiscountsCalculation = transactionAfterItemsDiscountAndBeforeTransactionDiscount.withItems(testUtilities.setCalculatedTotalAndRelativeDiscountOnItemsList(transactionAfterItemsDiscountAndBeforeTransactionDiscount.getItems(), relativeDiscountPercentageForNewAmountCalculation));

        // When
        Mono<Transaction> actualTransactionMono = transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionAfterItemsDiscountAndBeforeTransactionDiscount);

        // Then
        StepVerifier.create(actualTransactionMono).expectNext(transactionAfterAllDiscountsCalculation)
                .verifyComplete();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_TransactionWithDiscountBiggerThanTheTotalAmount_ThrowsException() {
        // Given
        Transaction transactionWithExcessiveDiscount = transaction.withTransactionLevelDiscount(BigDecimal.valueOf(10000000)); // Ensures the discount is larger than the total amount

        // When
        

        Mono<Transaction> actualTransactionMono = transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionWithExcessiveDiscount);

        // Then
        StepVerifier.create(actualTransactionMono)
                .expectError(InvalidDiscountAmountException.class)
                .verify();
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(nullTransaction));

        // Then
        assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }

    @Test
    void injectRecalculatedTotalAfterDiscount_transactionWithNullItemsPassed_ThrowsException() {
        // Given
        Transaction transactionWithNullItems = transaction.withItems(null);

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                transactionDiscountCalculator.injectRecalculatedTotalAfterDiscount(transactionWithNullItems));

        // Then
        assertEquals(nullPointerException.getMessage(), "items is marked non-null but is null");
    }
}