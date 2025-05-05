package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.UUID;
import static org.mockito.Mockito.mockStatic;

class CurrencyCheckerTest {

    private CurrencyChecker currencyChecker;
    UnitTestUtilities unitTestUtilities;
    TransactionDto transactionDto;



    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
        currencyChecker = new CurrencyChecker();
        transactionDto = unitTestUtilities.createTransactionDto(UUID.toString());
    }

    @Test
    void check_WithValidCurrency_ShouldReturnEmptyFlux() {
        // Given
        transactionDto = transactionDto.withCurrency("EUR");

        // When
        Flux<String> result = currencyChecker.check(transactionDto);

        // Then
        StepVerifier.create(result).verifyComplete();  // Expecting an empty flux, meaning no errors
    }

    @Test
    void check_WithInvalidCurrency_ShouldReturnErrorMessageFlux() {
        // Given
        transactionDto = transactionDto.withCurrency("INVALID_CURRENCY");

        // When
        Flux<String> result = currencyChecker.check(transactionDto);

        // Then
        StepVerifier.create(result)
                .expectNext(DtoErrorMessages.CURRENCY_IS_NOT_SUPPORTED)  // Expecting the error message flux
                .verifyComplete();
    }

    @Test
    void check_WithNullCurrency_ShouldReturnEmptyFlux() {
        // Given
        transactionDto = transactionDto.withCurrency(null);

        // When
        Flux<String> result = currencyChecker.check(transactionDto);

        // Then
        StepVerifier.create(result).verifyComplete();  // Expecting an empty flux, meaning no errors
    }
}
