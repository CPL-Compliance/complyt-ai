package com.complyt.business.strategy;

import com.complyt.domain.customer.exemption.ExemptionWrapper;
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
public class FunctionSelectorByExemptionCountryStrategyTest {
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
        testUtilities = new UnitTestUtilities(
                LocalDateTime.now(), UUID.randomUUID().toString());
    }

    // Concrete subclass for testing the abstract class
    private static class TestableFunctionSelectorByExemptionCountryStrategy extends FunctionSelectorByExemptionCountryStrategy {
        @Override
        protected Function<String, String> getFunctionForUsaOption(ExemptionWrapper exemptionWrapper) {
            return (someString) -> "USA Function";
        }

        @Override
        protected Function<String, String> getFunctionForNonUsaOption(ExemptionWrapper exemptionWrapper) {
            return (someString) -> "Non-USA Function";
        }
    }

    @Test
    void testSelectForUsaAddress() {
        ExemptionWrapper exemptionWrapper = testUtilities.createExemptionWrapper("");

        FunctionSelectorByExemptionCountryStrategy strategy = new TestableFunctionSelectorByExemptionCountryStrategy();

        assertEquals("USA Function", strategy.select(exemptionWrapper).apply("string"));
    }

    @Test
    void testSelectForNonUsaAddress() {
        ExemptionWrapper exemptionWrapper = testUtilities.createNonUsaExemptionWrapper("");

        FunctionSelectorByExemptionCountryStrategy strategy = new TestableFunctionSelectorByExemptionCountryStrategy();

        assertEquals("Non-USA Function", strategy.select(exemptionWrapper).apply("string"));
    }

}
