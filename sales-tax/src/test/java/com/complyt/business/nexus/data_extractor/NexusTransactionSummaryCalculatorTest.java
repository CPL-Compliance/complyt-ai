package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.TransactionNexusSummary;
import com.complyt.domain.transaction.Transaction;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NexusTransactionSummaryCalculatorTest {

    UnitTestUtilities unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant");
    private final NexusStateRule nexusStateRule = unitTestUtilities.createNexusStateRule("id");
    private final Transaction transaction = unitTestUtilities.createTransaction("id2");
    @InjectMocks
    private NexusTransactionSummaryCalculator nexusTransactionSummaryCalculator;
    @Mock
    private NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @Mock
    private TaxableCollectionAmountExtractor taxableCollectionAmountExtractor;

    @Test
    void extract_TaxableCollectionAmountExtractorReturnsAmount_ReturnsSummary() {
        // Given
        TransactionNexusSummary transactionNexusSummary = new TransactionNexusSummary(
                BigDecimal.valueOf(1200),
                transaction.getExternalTimestamps().getCreatedDate(),
                transaction.getTransactionType());

        // When
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nexusStateRule)).thenReturn(taxableCollectionAmountExtractor);
        when(taxableCollectionAmountExtractor.extract()).thenReturn(Mono.just(BigDecimal.valueOf(1200)));
        Mono<TransactionNexusSummary> transactionNexusSummaryMono = nexusTransactionSummaryCalculator.extract(transaction, nexusStateRule);

        // Then
        StepVerifier.create(transactionNexusSummaryMono).expectNext(transactionNexusSummary).verifyComplete();
    }

    @Test
    void extract_TaxableCollectionAmountExtractorReturnsEmpty_ReturnsEmpty() {
        // When
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nexusStateRule)).thenReturn(taxableCollectionAmountExtractor);
        when(taxableCollectionAmountExtractor.extract()).thenReturn(Mono.empty());
        Mono<TransactionNexusSummary> transactionNexusSummaryMono = nexusTransactionSummaryCalculator.extract(transaction, nexusStateRule);

        // Then
        StepVerifier.create(transactionNexusSummaryMono).expectNextCount(0).verifyComplete();
    }

    @Test
    void extract_nullTransaction_ReturnsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                nexusTransactionSummaryCalculator.extract(null, nexusStateRule));

        // Then
        assertEquals("transaction is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void extract_nullNexusStateRule_ReturnsNullPointerException() {
        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                nexusTransactionSummaryCalculator.extract(transaction, null));

        // Then
        assertEquals("nexusStateRule is marked non-null but is null", nullPointerException.getMessage());
    }

}