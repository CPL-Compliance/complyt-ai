package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.Taxable;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.transaction.ShippingFee;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaxableCollectionAmountExtractorTest {

    TaxableCollectionAmountExtractor taxableCollectionAmountExtractor;

    @Mock
    QualificationChecker qualificationChecker;

    Transaction transaction;
    NexusStateRule nexusStateRule;
    Customer customer;
    String nexusStateRuleId;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        nexusStateRuleId = UUID.randomUUID().toString();
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
        Transaction transactionBeforeCalculatedTotal = testUtilities.createTransaction(UUID.randomUUID().toString());
        ShippingFee shippingFee = transactionBeforeCalculatedTotal.getShippingFee();
        transaction = transactionBeforeCalculatedTotal
                .withItems(testUtilities.setCalculatedTotalOnItemList(transactionBeforeCalculatedTotal.getItems()))
                .withShippingFee(shippingFee.withCalculatedTotal(shippingFee.getTotalPrice()));
        nexusStateRule = testUtilities.createNexusStateRule(nexusStateRuleId);
        List<Taxable> taxables = testUtilities.createTaxables(transaction);
        taxableCollectionAmountExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, taxables, nexusStateRule);
    }

    @Test
    void extract_ExtractsTransactionItemsAmount_ReturnsAmount() {
        // Given
        ShippingFee nullShippingFee = null;
        Transaction transactionWithNoShippingFee = transaction.withShippingFee(nullShippingFee);

        // When
        when(qualificationChecker.isQualified(transactionWithNoShippingFee.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactionWithNoShippingFee.getItems().get(1), nexusStateRule)).thenReturn(false);
        BigDecimal expectedAmount = transactionWithNoShippingFee.getItems().get(0).getTotalPrice();
        Mono<BigDecimal> bigDecimalMono = taxableCollectionAmountExtractor.extract();

        StepVerifier.create(bigDecimalMono).expectNext(expectedAmount).verifyComplete();
    }

    @Test
    void extract_ExtractsTransactionItemsAmountAllItemsNotIncluded_ReturnsAmount() {
        // Given
        ShippingFee nullShippingFee = null;
        Transaction transactionWithNoShippingFee = transaction.withShippingFee(nullShippingFee);

        // When
        when(qualificationChecker.isQualified(transactionWithNoShippingFee.getItems().get(0), nexusStateRule)).thenReturn(false);
        when(qualificationChecker.isQualified(transactionWithNoShippingFee.getItems().get(1), nexusStateRule)).thenReturn(false);
        Mono<BigDecimal> bigDecimalMono = taxableCollectionAmountExtractor.extract();

        StepVerifier.create(bigDecimalMono).expectNextCount(0).verifyComplete();
    }

    @Test
    void extract_ExtractsTransactionItemsAndShippingFeeAmount_ReturnsAmount() {
        // Given

        // When
        when(qualificationChecker.isQualified(transaction.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transaction.getItems().get(1), nexusStateRule)).thenReturn(false);
        when(qualificationChecker.isQualified(transaction.getShippingFee(), nexusStateRule)).thenReturn(true);
        Mono<BigDecimal> bigDecimalMono = taxableCollectionAmountExtractor.extract();
        BigDecimal expectedAmount = transaction.getItems().get(0).getTotalPrice().add(transaction.getShippingFee().getTotalPrice());

        // Then
        StepVerifier.create(bigDecimalMono).expectNext(expectedAmount).verifyComplete();
    }

    @Test
    void equals_sameExtractor_ReturnsTrue() {
        // Given
        TaxableCollectionAmountExtractor givenTaxableCollectionAmountExtractor =
                new TaxableCollectionAmountExtractor(qualificationChecker, testUtilities.createTaxables(transaction), nexusStateRule);

        // When
        boolean isEquals = taxableCollectionAmountExtractor.equals(givenTaxableCollectionAmountExtractor);

        // Then
        assertTrue(isEquals);
    }
}
