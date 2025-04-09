package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;

public class ShippingAddressAlignerStrategyTest {

    ShippingAddressAlignmentStrategy shippingAddressAlignerStrategy;
    UsaAddressShippingAddressAligner usaAddressShippingAddressAligner;
    NonUsaAddressShippingAddressAligner nonUsaAddressShippingAddressAligner;
    Transaction transaction;
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
    void setUp() {
        shippingAddressAlignerStrategy = new ShippingAddressAlignmentStrategy(new UsaAddressShippingAddressAligner(), new NonUsaAddressShippingAddressAligner());
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void select_TransactionCountryIsUsa_RunsUsaFunction() {
        // Given
        ShippingAddress usaAbbreviationWithStateAddress = transaction.getShippingAddress()
                .withCountry("u.s.a")
                .withState("co");
        Transaction givenTransaction = transaction.withShippingAddress(usaAbbreviationWithStateAddress);

        ShippingAddress expectedAddress = transaction.getShippingAddress()
                .withCountry("USA")
                .withState("Colorado");
        Transaction exepctedTransaction = transaction.withShippingAddress(expectedAddress);

        // When + Then
        Transaction resultTransaction = (Transaction) shippingAddressAlignerStrategy.select(givenTransaction).apply(givenTransaction);
        Assertions.assertEquals(exepctedTransaction, resultTransaction);
    }

    @Test
    void select_TransactionCountryIsNotUsa_RunsNonUsaFunction() {
        // Given
        ShippingAddress nonUsaAbbreviationWithStateAddress = transaction.getShippingAddress()
                .withCountry("brazil");
        Transaction givenTransaction = transaction.withShippingAddress(nonUsaAbbreviationWithStateAddress);

        ShippingAddress expectedAddress = transaction.getShippingAddress()
                .withCountry("Brazil");
        Transaction exepctedTransaction = transaction.withShippingAddress(expectedAddress);

        // When + Then
        Transaction resultTransaction = (Transaction) shippingAddressAlignerStrategy.select(givenTransaction).apply(givenTransaction);
        Assertions.assertEquals(exepctedTransaction, resultTransaction);
    }

    @Test
    void select_TransactionCountryIsNotUsaAbbreviation_RunsNonUsaFunction() {
        // Given
        ShippingAddress nonUsaAbbreviationWithStateAddress = transaction.getShippingAddress()
                .withCountry("br");
        Transaction givenTransaction = transaction.withShippingAddress(nonUsaAbbreviationWithStateAddress);

        ShippingAddress expectedAddress = transaction.getShippingAddress()
                .withCountry("Brazil");
        Transaction exepctedTransaction = transaction.withShippingAddress(expectedAddress);

        // When + Then
        Transaction resultTransaction = (Transaction) shippingAddressAlignerStrategy.select(givenTransaction).apply(givenTransaction);
        Assertions.assertEquals(exepctedTransaction, resultTransaction);
    }
}