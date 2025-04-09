package com.complyt.business.transaction.items_amount;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.transaction.items_amounts.AmountCalculator;
import com.complyt.business.transaction.items_amounts.TransactionLevelTaxRateCalculator;
import com.complyt.domain.Taxable;
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TransactionLevelTaxRateCalculatorTest {

    @InjectMocks
    TransactionLevelTaxRateCalculator transactionLevelTaxRateCalculator;

    @Mock
    CollectionBuilder<Taxable> taxableCollectionBuilder;

    @Mock
    AmountCalculator<List<Taxable>> totalItemsAmountCalculator;

    private Transaction transaction;
    private UnitTestUtilities testUtilities;

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
    void setUp() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(new ObjectId().toString())
                .withSalesTax(new SalesTax(null, BigDecimal.valueOf(10), BigDecimal.valueOf(0.01), null, null));
    }

    @Test
    public void calculate_NullTransactionPassedAndTaxInclusive_ThrowsException() {
        // Given
        Transaction nullTransaction = null;

        // When
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transactionLevelTaxRateCalculator.calculate(nullTransaction, true));

        // Then
        assertEquals("transaction is marked non-null but is null", exception.getMessage());
    }

    @Test
    public void calculate_CalculatesRate_ReturnsRate() {
        // Given
        List<Taxable> taxables = List.of(
                transaction.getItems().get(0),
                transaction.getItems().get(1)
        );
        BigDecimal expectedRate = new BigDecimal("0.01000");

        // When
        when(taxableCollectionBuilder.build(transaction)).thenReturn(taxables);
        when(totalItemsAmountCalculator.calculate(taxables, false)).thenReturn(BigDecimal.valueOf(1000));
        BigDecimal rate = transactionLevelTaxRateCalculator.calculate(transaction, false);

        // Then
        Assertions.assertEquals(expectedRate, rate);
    }

}