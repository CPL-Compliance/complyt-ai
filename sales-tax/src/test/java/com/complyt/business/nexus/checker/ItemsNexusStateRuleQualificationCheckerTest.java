package com.complyt.business.nexus.checker;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.*;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
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

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        nexusStateRule = createNexusStateRule();
    }

    private Transaction createTransaction() {
        String id = null;
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = createItems();
        TimeStamps timeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null);
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
            add(TaxableCategory.TAXABLE);
        }};
        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};
        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, null, null, null);
    }

    private List<Item> createItems() {
        Item item = new Item(10, 5, 50, "description", "name", "C1S1", null,
                null, false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE);
        return new ArrayList<>() {{
            add(item);
        }};
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true, true,
                CalculationType.FIXED, "description", 0.5f, null);
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
