package com.complyt.v1.config;

import com.complyt.v1.models.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.UUID;

public class BodyCheckConfigTest {

    UnitTestUtilities unitTestUtilities;
    TransactionDto transactionDto;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
        transactionDto = unitTestUtilities.createTransactionDto(UUID.toString())
                .withShippingAddress(UnitTestUtilities.createAddressDtoInCalifornia());
    }

    @Test
    void transactionBodyCheck_PartialAddress_ReturnsTrue() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withPartial(true));

        // When + Then
        Mono<Boolean> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);

        StepVerifier.create(isValid).expectNext(true).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullStreet_ReturnsFalse() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withStreet(null));

        // When + Then
        Mono<Boolean> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);

        StepVerifier.create(isValid).expectNext(false).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullCity_ReturnsFalse() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withCity(null));

        // When + Then
        Mono<Boolean> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);

        StepVerifier.create(isValid).expectNext(false).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullCountry_ReturnsFalse() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withCountry(null));

        // When + Then
        Mono<Boolean> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);

        StepVerifier.create(isValid).expectNext(false).verifyComplete();
    }

}