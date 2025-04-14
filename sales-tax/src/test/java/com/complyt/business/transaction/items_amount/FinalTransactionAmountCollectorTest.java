package com.complyt.business.transaction.items_amount;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.transaction.items_amounts.AmountCalculator;
import com.complyt.business.transaction.items_amounts.FinalTransactionAmountCollector;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FinalTransactionAmountCollectorTest {

    @Mock
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    @Mock
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    @InjectMocks
    FinalTransactionAmountCollector finalTransactionAmountCollector;

    Transaction transaction;
    List<Taxable> taxables;
    BigDecimal calculatedAmount;

    UnitTestUtilities testUtilities;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    public void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
        taxables = new ArrayList<>(transaction.getItems());
        calculatedAmount = BigDecimal.valueOf(16000); // Example amount
    }

    @Test
    void collect_ValidTransaction_ReturnsTransactionWithFinalTransactionAmount() {
        // When
        when(taxableCollectionBuilder.build(transaction)).thenReturn(taxables);
        when(totalItemsAmountCalculator.calculate(taxables, false)).thenReturn(calculatedAmount);

        Transaction resultTransaction = finalTransactionAmountCollector.collect(transaction);

        // Then
        assertEquals(calculatedAmount, resultTransaction.getFinalTransactionAmount());
    }

    @Test
    void collect_NullTransactionPassed_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () ->
                finalTransactionAmountCollector.collect(nullTransaction));

        // Then
        Assertions.assertEquals(nullPointerException.getMessage(), "transaction is marked non-null but is null");
    }
}
