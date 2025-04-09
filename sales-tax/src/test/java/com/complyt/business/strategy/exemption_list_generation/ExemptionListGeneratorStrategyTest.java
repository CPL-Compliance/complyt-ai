package com.complyt.business.strategy.exemption_list_generation;

import com.complyt.domain.customer.exemption.Exemption;
import com.complyt.domain.customer.exemption.ExemptionWrapper;
import com.complyt.domain.transaction.Transaction;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

public class ExemptionListGeneratorStrategyTest {

    ExemptionListGeneratorStrategy exemptionListGeneratorStrategy;
    ExemptionListGenerator usaAddressExemptionListGenerator;
    ExemptionListGenerator nonUsaAddressExemptionListGenerator;
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
        exemptionListGeneratorStrategy = new ExemptionListGeneratorStrategy(new UsaAddressExemptionListGenerator(), new NonUsaAddressExemptionListGenerator());
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transaction = testUtilities.createTransaction(UUID.randomUUID().toString());
    }

    @Test
    void select_ExemptionCountryIsUsa_RunsUsaFunction() {
        // Given
        ExemptionWrapper exemptionWrapper = testUtilities.createExemptionWrapper("id");
        List<Exemption> expectedExemptions = testUtilities.createExemptionsListFromWrapper(exemptionWrapper);

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());


        // Then
        Flux<Exemption> actualFlux = (Flux<Exemption>) exemptionListGeneratorStrategy.select(exemptionWrapper).apply(exemptionWrapper);
        StepVerifier.create(actualFlux).expectNext(expectedExemptions.get(0)).verifyComplete();
    }

    @Test
    void select_ExemptionCountryIsNotUsa_RunsNonUsaFunction() {
        // Given
        ExemptionWrapper exemptionWrapper = testUtilities.createNonUsaExemptionWrapper("id");
        List<Exemption> expectedExemptions = testUtilities.createNonUsaExemptionsListFromWrapper(exemptionWrapper);

        // When
        when(TenantResolver.resolve()).thenReturn(Mono.empty());

        // Then
        Flux<Exemption> actualFlux = (Flux<Exemption>) exemptionListGeneratorStrategy.select(exemptionWrapper).apply(exemptionWrapper);
        StepVerifier.create(actualFlux).expectNext(expectedExemptions.get(0)).verifyComplete();
    }
}
