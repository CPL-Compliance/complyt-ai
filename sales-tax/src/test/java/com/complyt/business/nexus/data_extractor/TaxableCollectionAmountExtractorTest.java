package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.ShippingFee;
import com.complyt.domain.Taxable;
import com.complyt.domain.Transaction;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.ObjectStub;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    ObjectId customerId;
    String nexusStateRuleId;

    ObjectStub objectStub;

    @BeforeEach
    void setUp() {
        objectStub = new ObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        nexusStateRuleId = UUID.randomUUID().toString();
        customer = objectStub.createCustomer(UUID.randomUUID().toString());
        transaction = objectStub.createTransaction(UUID.randomUUID().toString());
        nexusStateRule = objectStub.createNexusStateRule(nexusStateRuleId);
        List<Taxable> taxables = objectStub.createTaxables(transaction);
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
        float expectedAmount = transactionWithNoShippingFee.getItems().get(0).getTotalPrice();
        float amount = taxableCollectionAmountExtractor.extract();

        // Then
        assertEquals(expectedAmount, amount);
    }

    @Test
    void extract_ExtractsTransactionItemsAndShippingFeeAmount_ReturnsAmount() {
        // Given

        // When
        when(qualificationChecker.isQualified(transaction.getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transaction.getItems().get(1), nexusStateRule)).thenReturn(false);
        when(qualificationChecker.isQualified(transaction.getShippingFee(), nexusStateRule)).thenReturn(true);
        float amount = taxableCollectionAmountExtractor.extract();
        float expectedAmount = transaction.getItems().get(0).getTotalPrice() + transaction.getShippingFee().getTotalPrice();

        // Then
        assertEquals(amount, expectedAmount);
    }

    @Test
    void equals_sameExtractor_ReturnsTrue() {
        // Given
        TaxableCollectionAmountExtractor givenTaxableCollectionAmountExtractor =
                new TaxableCollectionAmountExtractor(qualificationChecker, objectStub.createTaxables(transaction), nexusStateRule);

        // When
        boolean isEquals = taxableCollectionAmountExtractor.equals(givenTaxableCollectionAmountExtractor);

        // Then
        assertTrue(isEquals);
    }
}
