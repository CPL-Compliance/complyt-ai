package com.complyt.utils.factory;

import com.complyt.business.builder.TaxableCollectionBuilder;
import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.business.nexus.data_extractor.TaxableCollectionAmountExtractor;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NexusAmountAggregatorFactoryTest {

    @InjectMocks
    NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @Mock
    QualificationChecker qualificationChecker;

    @Mock
    TaxableCollectionBuilder taxableCollectionBuilder;

    Transaction transaction;
    NexusStateRule nexusStateRule;
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
        nexusStateRule = domainObjectStub.createNexusStateRule(UUID.randomUUID().toString());
    }

    @Test
    void createTaxableCollectionAmountExtractor_CreatesAggregatorWithItemsAndShippingFeeExtractors_ReturnsExtractor() {
        // Given
        List<Taxable> taxables = new ArrayList<>(transaction.getItems());
        taxables.add(transaction.getShippingFee());
        TaxableCollectionAmountExtractor expectedExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, taxables, nexusStateRule);

        // When
        when(taxableCollectionBuilder.build(transaction)).thenReturn(taxables);
        TaxableCollectionAmountExtractor actualExtractor = nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nexusStateRule);

        // Then
        assertEquals(expectedExtractor, actualExtractor);
    }

    @Test
    void createTaxableCollectionAmountExtractor_ShippingFeeIsNull_ReturnsExtractorWithoutShippingFeeInTaxableList() {
        // Given
        Transaction transactionWithNullShippingFee = transaction.withShippingFee(null);
        List<Taxable> taxables = new ArrayList<>(transactionWithNullShippingFee.getItems());

        TaxableCollectionAmountExtractor expectedExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, taxables, nexusStateRule);

        // When
        when(taxableCollectionBuilder.build(transactionWithNullShippingFee)).thenReturn(taxables);
        TaxableCollectionAmountExtractor actualExtractor = nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactionWithNullShippingFee, nexusStateRule);

        // Then
        assertEquals(expectedExtractor, actualExtractor);
    }

    @Test
    void createTaxableCollectionAmountExtractor_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(nullTransaction, nexusStateRule));

        // Then
        assertEquals("transaction is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void createTaxableCollectionAmountExtractor_NullNexusStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transaction, nullNexusStateRule));

        // Then
        assertEquals("nexusStateRule is marked non-null but is null", nullPointerException.getMessage());
    }

}
