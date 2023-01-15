package com.complyt.utils.factory;

import com.complyt.business.builder.TaxableCollectionBuilder;
import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.business.nexus.data_extractor.TaxableCollectionAmountExtractor;
import com.complyt.domain.*;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

    @BeforeEach
    void setUp() {
        transaction = createTransaction();
        nexusStateRule = createNexusStateRule();
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
    }

    private Transaction createTransaction() {
        String id = null;
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
        ComplytTimestamp complytTimestamp = new ComplytTimestamp(LocalDateTime.now());
        Timestamps timeStamps = new Timestamps(complytTimestamp, complytTimestamp);
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null, 0, 0, 0);
    }

    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, jurisdictionalSalesTaxRules,
                new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
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
