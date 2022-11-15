package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NexusCalculatorTest {

    @InjectMocks
    NexusCalculator nexusCalculator;

    @Mock
    NexusTransactionAmountExtractor nexusTransactionAmountExtractor;

    @Mock
    NexusTransactionCountExtractor nexusTransactionCountExtractor;

    @Mock
    TransactionsFilterByNexusRules transactionNexusFilter;


    private Transaction createTransaction() {
        String id = UUID.randomUUID().toString();
        ObjectId tenantId = new ObjectId();
        String externalId = UUID.randomUUID().toString();
        ObjectId customerId = new ObjectId();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        items.add(new Item(2000, 4, 8000, "description", "name", "taxCode", null, salesTaxRate, false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE));
        Customer customer = new Customer(customerId.toString(), UUID.randomUUID().toString(), "customer", shippingAddress, tenantId.toString(), CustomerType.RETAIL);
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, tenantId.toString(), null, null, TransactionType.INVOICE, null);
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

    private List<Transaction> createTransactionsList() {
        Transaction transaction = createTransaction();
        Transaction secondTransaction = transaction.withId(UUID.randomUUID().toString()).withExternalId(UUID.randomUUID().toString());
        return new ArrayList<>() {{
            add(transaction);
            add(secondTransaction);
        }};
    }

    @Test
    void calculate_CalculatesNexusData_ReturnsSummary() {
        // Given
        List<Transaction> transactions = createTransactionsList();

        int count = transactions.size();
        float amount = transactions.get(0).getItems().get(0).getTotalPrice() + transactions.get(1).getItems().get(0).getTotalPrice();
        NexusCalculationSummary summary = new NexusCalculationSummary(count, amount);
        NexusStateRule nexusStateRule = createNexusStateRule();

        // When
        when(transactionNexusFilter.filter(transactions, nexusStateRule)).thenReturn(transactions);
        when(nexusTransactionCountExtractor.extract(transactions.get(0), nexusStateRule)).thenReturn(1);
        when(nexusTransactionCountExtractor.extract(transactions.get(1), nexusStateRule)).thenReturn(1);
        when(nexusTransactionAmountExtractor.extract(transactions.get(0), nexusStateRule)).thenReturn(transactions.get(0).getItems().get(0).getTotalPrice());
        when(nexusTransactionAmountExtractor.extract(transactions.get(1), nexusStateRule)).thenReturn(transactions.get(1).getItems().get(0).getTotalPrice());

        NexusCalculationSummary actualSummary = nexusCalculator.calculate(transactions, nexusStateRule);

        // Then
        assertEquals(summary, actualSummary);
    }

    @Test
    void calculate_CustomerTypeDoesNotExist_ReturnsSummary() {
        // Given
        List<Transaction> transactions = createTransactionsList();
        int count = 0;
        float amount = 0;
        NexusCalculationSummary summary = new NexusCalculationSummary(count, amount);
        List<CustomerType> resellerCustomerOnly = new ArrayList<>() {{
            add(CustomerType.RESELLER);
        }};
        NexusStateRule nexusStateRule = createNexusStateRule().withCustomerTypes(resellerCustomerOnly);

        // When
        NexusCalculationSummary actualSummary = nexusCalculator.calculate(transactions, nexusStateRule);

        // Then
        assertEquals(summary, actualSummary);
    }

}
