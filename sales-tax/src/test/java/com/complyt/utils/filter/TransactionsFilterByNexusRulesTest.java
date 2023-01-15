package com.complyt.utils.filter;

import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.TransactionType;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.timestamps.ComplytTimestamp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.DomainObjectStub;

import java.time.LocalDateTime;
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
    DomainObjectStub domainObjectStub;

    @BeforeEach
    void setUp() {
        domainObjectStub = new DomainObjectStub(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transactionsFilterByNexusRules = new TransactionsFilterByNexusRules();
        customer = domainObjectStub.createCustomer(UUID.randomUUID().toString());
        transactions = createTransactionList();
        nexusStateRule = domainObjectStub.createNexusStateRule(UUID.randomUUID().toString());
    }

    private List<Transaction> createTransactionList() {
        invoiceTransaction = domainObjectStub.createTransaction(UUID.randomUUID().toString());
        salesOrderTransaction = invoiceTransaction
                .withId(UUID.randomUUID().toString())
                .withExternalId(UUID.randomUUID().toString())
                .withTransactionType(TransactionType.SALES_ORDER);


        return new ArrayList<>() {{
            add(invoiceTransaction);
            add(salesOrderTransaction);
        }};
    }

    @Test
    void filter_FiltersBecauseCustomerTypeDoesNotExist_ReturnsOneTransaction() {
        // Given
        transactions.remove(salesOrderTransaction);
        Customer marketPlaceCustomer = customer.withCustomerType(CustomerType.MARKETPLACE).withComplytId(customer.getComplytId());
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
    void filter_FiltersBecauseTransactionIsOfStatusCancelled_ReturnsZeroTransactions() {
        // Given
        Transaction transaction = invoiceTransaction.withTransactionStatus(TransactionStatus.CANCELLED);
        List<Transaction> transactions = new ArrayList<>() {{
            add(transaction);
        }};

        // When
        List<Transaction> filteredTransactions = transactionsFilterByNexusRules.filter(transactions, nexusStateRule);

        // Then
        assertNotNull(filteredTransactions);
        assertEquals(0, filteredTransactions.size());
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
