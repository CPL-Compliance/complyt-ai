package com.complyt.business.nexus.data_extractor;

import com.complyt.domain.State;
import com.complyt.domain.transaction.Transaction;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusThreshold;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.utils.filter.TransactionsFilterByNexusRules;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NexusCalculatorTest {

    @InjectMocks
    NexusCalculator nexusCalculator;

    @Mock
    NexusTransactionsAmountCalculator nexusTransactionsAmountCalculator;

    @Mock
    NexusTransactionsCountCalculator nexusTransactionsCountCalculator;

    @Mock
    TransactionsFilterByNexusRules transactionNexusFilter;


    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
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

    private List<Transaction> createTransactionsList() {
        Transaction transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
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
        BigDecimal amount = transactions.get(0).getItems().get(0).getTotalPrice().add(transactions.get(1).getItems().get(0).getTotalPrice());
        NexusCalculationSummary summary = new NexusCalculationSummary(count, amount);
        NexusStateRule nexusStateRule = createNexusStateRule();

        // When
        when(transactionNexusFilter.filter(transactions, nexusStateRule)).thenReturn(transactions);
        when(nexusTransactionsCountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(count));
        when(nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(amount));

        Mono<NexusCalculationSummary> actualSummary = nexusCalculator.calculate(transactions, nexusStateRule);

        // Then
        StepVerifier.create(actualSummary).expectNext(summary).verifyComplete();
    }

    @Test
    void calculate_CustomerTypeDoesNotExist_ReturnsSummary() {
        // Given
        List<Transaction> transactions = createTransactionsList();
        int count = 0;
        BigDecimal amount = BigDecimal.ZERO;
        NexusCalculationSummary summary = new NexusCalculationSummary(count, amount);
        List<CustomerType> resellerCustomerOnly = new ArrayList<>() {{
            add(CustomerType.RESELLER);
        }};
        NexusStateRule nexusStateRule = createNexusStateRule().withCustomerTypes(resellerCustomerOnly);

        // When
        when(transactionNexusFilter.filter(transactions, nexusStateRule)).thenReturn(transactions);
        when(nexusTransactionsCountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(count));
        when(nexusTransactionsAmountCalculator.extract(transactions, nexusStateRule)).thenReturn(Mono.just(amount));
        Mono<NexusCalculationSummary> actualSummary = nexusCalculator.calculate(transactions, nexusStateRule);

        // Then
        StepVerifier.create(actualSummary).expectNext(summary).verifyComplete();
    }
}