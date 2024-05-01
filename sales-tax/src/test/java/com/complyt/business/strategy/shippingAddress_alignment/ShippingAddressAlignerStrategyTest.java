package com.complyt.business.strategy.shippingAddress_alignment;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.domain.transaction.Address;
import com.complyt.domain.transaction.Transaction;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
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
        Address usaAbbreviationWithStateAddress = transaction.getShippingAddress()
                .withCountry("u.s.a")
                .withState("co");
        Transaction givenTransaction = transaction.withShippingAddress(usaAbbreviationWithStateAddress);

        Address expectedAddress = transaction.getShippingAddress()
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
        Address nonUsaAbbreviationWithStateAddress = transaction.getShippingAddress()
                .withCountry("brazil");
        Transaction givenTransaction = transaction.withShippingAddress(nonUsaAbbreviationWithStateAddress);

        Address expectedAddress = transaction.getShippingAddress()
                .withCountry("BRAZIL");
        Transaction exepctedTransaction = transaction.withShippingAddress(expectedAddress);

        // When + Then
        Transaction resultTransaction = (Transaction) shippingAddressAlignerStrategy.select(givenTransaction).apply(givenTransaction);
        Assertions.assertEquals(exepctedTransaction, resultTransaction);
    }

    @Test
    void select_TransactionCountryIsNotUsaAbbreviation_RunsNonUsaFunction() {
        // Given
        Address nonUsaAbbreviationWithStateAddress = transaction.getShippingAddress()
                .withCountry("br");
        Transaction givenTransaction = transaction.withShippingAddress(nonUsaAbbreviationWithStateAddress);

        Address expectedAddress = transaction.getShippingAddress()
                .withCountry("BRAZIL");
        Transaction exepctedTransaction = transaction.withShippingAddress(expectedAddress);

        // When + Then
        Transaction resultTransaction = (Transaction) shippingAddressAlignerStrategy.select(givenTransaction).apply(givenTransaction);
        Assertions.assertEquals(exepctedTransaction, resultTransaction);
    }
}