package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.qualification_check.QualificationChecker;
import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.utils.factory.NexusAmountAggregatorFactory;
import lombok.NonNull;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NexusTransactionsAmountCalculatorTest {

    @InjectMocks
    NexusTransactionsAmountCalculator nexusTransactionsAmountCalculator;

    @Mock
    NexusAmountAggregatorFactory nexusAmountAggregatorFactory;

    @Mock
    QualificationChecker qualificationChecker;

    List<Transaction> transactions;
    NexusStateRule nexusStateRule;

    @BeforeEach
    void setUp() {
        transactions = createTransactions();
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
        String id = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        String tenantId = UUID.randomUUID().toString();
        List<Item> items = new ArrayList<>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE
                ));
            }
        };
        Customer customer = createCustomer(customerId);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, tenantId, null, new TimeStamps(LocalDateTime.now(), LocalDateTime.now()), TransactionType.INVOICE, null, null);
    }

    private Transaction createRefundTransaction() {
        return transactions.get(0)
                .withId(UUID.randomUUID().toString())
                .withExternalId(UUID.randomUUID().toString())
                .withTransactionType(TransactionType.REFUND);
    }

    private List<Transaction> createTransactions() {
        Transaction transaction = createTransaction();
        List<Item> secondTransactionItems = new ArrayList<>() {{
            add(transaction.getItems().get(0).withUnitPrice(1000).withTotalPrice(4000));
        }};

        Transaction secondTransaction = transaction
                .withId(UUID.randomUUID().toString())
                .withExternalId(UUID.randomUUID().toString())
                .withItems(secondTransactionItems);

        return new ArrayList<>() {{
            add(transaction);
            add(secondTransaction);
        }};
    }

    private Customer createCustomer(@NonNull ObjectId customerId) {
        String tenantId = UUID.randomUUID().toString();
        String externalId = UUID.randomUUID().toString();
        String name = "Existing Customer";
        Address address = new Address("City", "Country", "County", "State", "Street", "Zip");
        return new Customer(customerId.toString(), externalId, name, address, tenantId, CustomerType.RETAIL);
    }

    @Test
    void extract_ExtractsAmountOfInvoices_ReturnsAmount() {
        // Given
        float expectedTotalAmount = transactions.get(0).getItems().get(0).getTotalPrice() +
                transactions.get(1).getItems().get(0).getTotalPrice();
        List<Taxable> firstTransactionTaxables = new ArrayList<>(transactions.get(0).getItems());
        List<Taxable> secondTransactionTaxables = new ArrayList<>(transactions.get(1).getItems());
        TaxableCollectionAmountExtractor firstExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, firstTransactionTaxables, nexusStateRule);
        TaxableCollectionAmountExtractor secondExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, secondTransactionTaxables, nexusStateRule);

        // When
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(0), nexusStateRule)).thenReturn(firstExtractor);
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(1), nexusStateRule)).thenReturn(secondExtractor);
        when(qualificationChecker.isQualified(transactions.get(0).getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(1).getItems().get(0), nexusStateRule)).thenReturn(true);
        Mono<Float> actualTotalAmount = nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule);

        // Then
        StepVerifier.create(actualTotalAmount).expectNext(expectedTotalAmount).verifyComplete();
    }

    @Test
    void extract_ThirdTransactionIsRefund_ReturnsAmount() {
        // Given
        Transaction refundTransaction = createRefundTransaction();
        transactions.add(refundTransaction);
        float expectedTotalAmount = transactions.get(0).getItems().get(0).getTotalPrice() +
                transactions.get(1).getItems().get(0).getTotalPrice() - refundTransaction.getItems().get(0).getTotalPrice();
        List<Taxable> firstTransactionTaxables = new ArrayList<>(transactions.get(0).getItems());
        List<Taxable> secondTransactionTaxables = new ArrayList<>(transactions.get(1).getItems());
        List<Taxable> thirdTransactionTaxables = new ArrayList<>(transactions.get(2).getItems());
        TaxableCollectionAmountExtractor firstExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, firstTransactionTaxables, nexusStateRule);
        TaxableCollectionAmountExtractor secondExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, secondTransactionTaxables, nexusStateRule);
        TaxableCollectionAmountExtractor thirdExtractor = new TaxableCollectionAmountExtractor(qualificationChecker, thirdTransactionTaxables, nexusStateRule);


        // When
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(0), nexusStateRule)).thenReturn(firstExtractor);
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(1), nexusStateRule)).thenReturn(secondExtractor);
        when(nexusAmountAggregatorFactory.createTaxableCollectionAmountExtractor(transactions.get(2), nexusStateRule)).thenReturn(thirdExtractor);
        when(qualificationChecker.isQualified(transactions.get(0).getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(1).getItems().get(0), nexusStateRule)).thenReturn(true);
        when(qualificationChecker.isQualified(transactions.get(2).getItems().get(0), nexusStateRule)).thenReturn(true);
        Mono<Float> actualTotalAmount = nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule);

        // Then
        StepVerifier.create(actualTotalAmount).expectNext(expectedTotalAmount).verifyComplete();
    }

    @Test
    void extract_NullTransactionPassed_ThrowsException() {
        // Given
        List<Transaction> nullTransactions = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsAmountCalculator.extract(nullTransactions, nexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "transactions is marked non-null but is null");
    }

    @Test
    void extract_NullStateRulePassed_ThrowsException() {
        // Given
        NexusStateRule nullNexusStateRule = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsAmountCalculator.extract(transactions, nullNexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }
}
