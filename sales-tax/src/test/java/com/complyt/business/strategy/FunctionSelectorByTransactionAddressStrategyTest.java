package com.complyt.business.strategy;

import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
public class FunctionSelectorByTransactionAddressStrategyTest {
    UnitTestUtilities testUtilities;

   

    @BeforeEach
    void setUp() {
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
    }

    // Concrete subclass for testing the abstract class
    private static class TestableFunctionSelectorByAddressStrategy extends FunctionSelectorByTransactionAddressStrategy {
        @Override
        protected Function<String, String> getFunctionForNonUsaOption(Transaction transaction) {
            return (someString) -> "Non-USA Function";
        }

        @Override
        protected Function<String, String> getFunctionForUsaOption(Transaction transaction) {
            return (someString) -> "USA Function";
        }
    }

    @Test
    void testSelectForUsaAddress() {
        Transaction transaction = testUtilities.createTransaction("");

        FunctionSelectorByTransactionAddressStrategy strategy = new TestableFunctionSelectorByAddressStrategy();

        assertEquals("USA Function", strategy.select(transaction).apply("string"));
    }

    @Test
    void testSelectForNonUsaAddress() {
        Transaction transaction = testUtilities.createGtTransaction("");
        transaction = transaction.
                withShippingAddress(transaction.getShippingAddress().withCountry("CANADA"));

        FunctionSelectorByTransactionAddressStrategy strategy = new TestableFunctionSelectorByAddressStrategy();

        assertEquals("Non-USA Function", strategy.select(transaction).apply("string"));
    }

}
