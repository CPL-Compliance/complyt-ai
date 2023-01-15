package com.complyt.business.nexus.checker;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.*;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.bson.types.ObjectId;
import org.javatuples.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ItemsNexusStateRuleQualificationCheckerTest {

    @InjectMocks
    ItemsNexusStateRuleQualificationChecker itemsNexusStateRuleQualificationChecker;

    @Mock
    QualificationChecker qualificationChecker;
    Transaction transaction;
    private NexusStateRule nexusStateRule;

    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transaction = domainObjectStub.createTransaction(null);
        nexusStateRule = domainObjectStub.createNexusStateRule(UUID.randomUUID().toString());
}

    @Test
    void check_NoItemsThatCountsRegardingToNexusRule_ReturnsFalse() {
        // Given
        Pair<List<Taxable>, NexusStateRule> nexusStateRulePair = new Pair(transaction.getItems(), nexusStateRule);
        when(qualificationChecker.isQualified(transaction.getItems().get(0), nexusStateRule)).thenReturn(false);

        // When + Then
        boolean doItemsCount = itemsNexusStateRuleQualificationChecker.check(nexusStateRulePair);
        assertFalse(doItemsCount);
    }

    @Test
    void check_ThereIsItemThatCountsRegardingToNexusRule_ReturnsTrue() {
        // Given
        TangibleCategory tangibleCategory = nexusStateRule.getTangibleCategories().get(0);
        TaxableCategory taxableCategory = nexusStateRule.getTaxableCategories().get(0);
        Item itemThatCounts = transaction.getItems().get(0).withTangibleCategory(tangibleCategory).withTaxableCategory(taxableCategory);
        transaction.getItems().add(itemThatCounts);

        when(qualificationChecker.isQualified(transaction.getItems().get(0), nexusStateRule)).thenReturn(true);

        Pair<List<Taxable>, NexusStateRule> nexusStateRulePair = new Pair(transaction.getItems(), nexusStateRule);

        // When + Then
        boolean doItemsCount = itemsNexusStateRuleQualificationChecker.check(nexusStateRulePair);
        assertTrue(doItemsCount);
    }

    @Test
    void check_OnlyShippingFeeQualifies_ReturnsFalse() {
        // Given
        TangibleCategory tangibleCategory = nexusStateRule.getTangibleCategories().get(0);
        TaxableCategory taxableCategory = nexusStateRule.getTaxableCategories().get(0);
        ShippingFee shippingFeeThatCounts = transaction.getShippingFee().withTangibleCategory(tangibleCategory).withTaxableCategory(taxableCategory);
        Transaction transactionWithShippingFeeThatCounts = transaction.withShippingFee(shippingFeeThatCounts);

        when(qualificationChecker.isQualified(transactionWithShippingFeeThatCounts.getItems().get(0), nexusStateRule)).thenReturn(false);

        Pair<List<Taxable>, NexusStateRule> nexusStateRulePair = new Pair(transaction.getItems(), nexusStateRule);

        // When + Then
        boolean doItemsCount = itemsNexusStateRuleQualificationChecker.check(nexusStateRulePair);
        assertFalse(doItemsCount);
    }

    @Test
    void check_NullPairPassed_ThrowsException() {
        // Given
        Pair<List<Taxable>, NexusStateRule> nexusStateRulePair = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            itemsNexusStateRuleQualificationChecker.check(nexusStateRulePair);
        });

        assertEquals(nullPointerException.getMessage(), "itemsAndRule is marked non-null but is null");
    }
}
