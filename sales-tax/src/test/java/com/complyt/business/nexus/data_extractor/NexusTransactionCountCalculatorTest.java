package com.complyt.business.nexus.data_extractor;

import com.complyt.business.nexus.checker.ItemsNexusStateRuleQualificationChecker;
import com.complyt.domain.transaction.Item;
import com.complyt.domain.State;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.transaction.TransactionType;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class NexusTransactionCountCalculatorTest {

    @InjectMocks
    NexusTransactionsCountCalculator nexusTransactionsCountCalculator;

    @Mock
    ItemsNexusStateRuleQualificationChecker itemsNexusStateRuleQualificationChecker;

    List<Transaction> transactions;
    NexusStateRule nexusStateRule;
    Customer customer;
    ObjectId customerId;

    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        customer = testUtilities.createCustomer(UUID.randomUUID().toString());
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

        NexusThreshold nexusThreshold = new NexusThreshold(new BigDecimal(1000), 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(UUID.randomUUID().toString(), true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
    }

    private List<Transaction> createTransactions() {

        Transaction transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        return new ArrayList<>() {{
            add(transaction);
        }};
    }

    @Test
    void extract_ExtractsTransactionItemsCount_ReturnsShouldBeCounted() {
        // Given

        // When
        when(itemsNexusStateRuleQualificationChecker.check(new Pair(transactions.get(0).getItems(), nexusStateRule))).thenReturn(true);
        Mono<Integer> count = nexusTransactionsCountCalculator.extract(transactions, nexusStateRule);

        // Then
        StepVerifier.create(count).expectNext(1).verifyComplete();
    }

    @Test
    void extract_ExtractsTransactionItemsCount_ReturnsShouldNotBeCountedBecauseItemsDontQualify() {
        // Given
        List<Item> items = new ArrayList<>() {{
            add(transactions.get(0).getItems().get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE));
        }};
        Transaction otherTransaction = transactions.get(0).withItems(items);
        List<Transaction> otherList = new ArrayList<>() {{
            add(otherTransaction);
        }};

        // When
        when(itemsNexusStateRuleQualificationChecker.check(new Pair(otherTransaction.getItems(), nexusStateRule))).thenReturn(false);
        Mono<Integer> count = nexusTransactionsCountCalculator.extract(otherList, nexusStateRule);

        // Then
        StepVerifier.create(count).expectNext(0).verifyComplete();
    }

    @Test
    void extract_ExtractsTransactionItemsCount_ReturnsShouldNotBeCountedBecauseTransactionIsOfTypeRefund() {
        // Given
        Transaction refundTransaction = transactions.get(0).withTransactionType(TransactionType.REFUND);
        List<Transaction> transactions = new ArrayList<>() {{
            add(refundTransaction);
        }};

        // When
        when(itemsNexusStateRuleQualificationChecker.check(new Pair(refundTransaction.getItems(), nexusStateRule))).thenReturn(true);
        Mono<Integer> count = nexusTransactionsCountCalculator.extract(transactions, nexusStateRule);

        // Then
        StepVerifier.create(count).expectNext(0).verifyComplete();
    }

    @Test
    void extract_NullTransactionPassed_ThrowsException() {
        // Given
        List<Transaction> nullTransactions = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            nexusTransactionsCountCalculator.extract(nullTransactions, nexusStateRule);
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
            nexusTransactionsCountCalculator.extract(transactions, nullNexusStateRule);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "nexusStateRule is marked non-null but is null");
    }

}
