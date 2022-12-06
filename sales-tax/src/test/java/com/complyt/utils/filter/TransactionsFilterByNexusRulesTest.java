package com.complyt.utils.filter;

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
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TransactionsFilterByNexusRulesTest {

    TransactionsFilterByNexusRules transactionsFilterByNexusRules;
    List<Transaction> transactions;
    NexusStateRule nexusStateRule;
    Transaction invoiceTransaction;
    Transaction salesOrderTransaction;
    Customer customer;

    @BeforeEach
    void setUp() {
        transactionsFilterByNexusRules = new TransactionsFilterByNexusRules();
        transactions = createTransactionList();
        nexusStateRule = createNexusStateRule();
    }

    private List<Transaction> createTransactionList() {
        String id = UUID.randomUUID().toString();
        ObjectId tenantId = new ObjectId();
        ObjectId customerId = new ObjectId();
        String externalId = UUID.randomUUID().toString();
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<>();
        SalesTaxRate salesTaxRate = new SalesTaxRate(0.01f, 0.01f, 0.01f, 0.01f, 0.01f, 0.05f);
        items.add(new Item(2000, 4, 8000, "description", "name", "taxCode", null, salesTaxRate, false, 0, TangibleCategory.TANGIBLE, TaxableCategory.TAXABLE));
        customer = createCustomer(customerId, tenantId, shippingAddress);
        invoiceTransaction = new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, customer, null, TransactionStatus.ACTIVE, tenantId.toString(), null, null, TransactionType.INVOICE, null);
        salesOrderTransaction = invoiceTransaction
                .withId(UUID.randomUUID().toString())
                .withExternalId(UUID.randomUUID().toString())
                .withTransactionType(TransactionType.SALES_ORDER);


        return new ArrayList<>() {{
            add(invoiceTransaction);
            add(salesOrderTransaction);
        }};
    }

    private Customer createCustomer(ObjectId customerId, ObjectId tenantId, Address shippingAddress) {
        return new Customer(customerId.toString(), UUID.randomUUID().toString(), "customer", shippingAddress, tenantId.toString(), CustomerType.RETAIL);
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

    @Test
    void filter_FiltersBecauseCustomerTypeDoesNotExist_ReturnsOneTransaction() {
        // Given
        transactions.remove(salesOrderTransaction);
        Customer marketPlaceCustomer = customer.withCustomerType(CustomerType.MARKETPLACE);
        Transaction transactionWithCustomerThatDoesNotExist = invoiceTransaction.withCustomer(marketPlaceCustomer);
        transactions.add(transactionWithCustomerThatDoesNotExist);

        // When
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);

        // Then
        assertNotNull(filteredTransactions);
        assertEquals(1, filteredTransactions.size());
        assertEquals(filteredTransactions.get(0), invoiceTransaction);
    }

    @Test
    void filter_FiltersBecauseTransactionIsOfTypeSalesOrder_ReturnsOneTransaction() {
        // Given

        // When
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);

        // Then
        assertNotNull(filteredTransactions);
        assertEquals(1, filteredTransactions.size());
        assertEquals(filteredTransactions.get(0), invoiceTransaction);
    }

    @Test
    void filter_BothTransactionsQualify_ReturnsTwoTransactions() {
        // Given
        Transaction secondInvoiceTransaction = salesOrderTransaction.withTransactionType(TransactionType.INVOICE);
        transactions.remove(salesOrderTransaction);
        transactions.add(secondInvoiceTransaction);

        // When
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);

        // Then
        assertNotNull(filteredTransactions);
        assertEquals(2, filteredTransactions.size());
        assertEquals(filteredTransactions.get(0), invoiceTransaction);
        assertEquals(filteredTransactions.get(1), secondInvoiceTransaction);
    }

}
