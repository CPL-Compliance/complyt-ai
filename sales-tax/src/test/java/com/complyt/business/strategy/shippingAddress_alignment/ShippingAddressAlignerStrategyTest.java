package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.ShippingAddress;
import com.complyt.domain.transaction.Transaction;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

public class ShippingAddressAlignerStrategyTest {

    ShippingAddressAlignmentStrategy shippingAddressAlignerStrategy;
    UsaAddressShippingAddressAligner usaAddressShippingAddressAligner;
    NonUsaAddressShippingAddressAligner nonUsaAddressShippingAddressAligner;
    Transaction transaction;
    UnitTestUtilities testUtilities;


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