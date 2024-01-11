package com.complyt.v1.config;

import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
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

//    @Test //todo: fix
//    void transactionBodyCheck_PartialAddress_ReturnsEmptyFlux() {
//        // Given
//        TransactionDto transactionToCheck = transactionDto.withShippingAddress
//                (transactionDto.shippingAddress().withPartial(true));
//
//        // When + Then
//        Flux<String> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);
//
//        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
//    }

//    @Test //todo: fix
//    void transactionBodyCheck_PartialFalseAndNullStreet_ReturnsErrorMessage() {
//        // Given
//        TransactionDto transactionToCheck = transactionDto.withShippingAddress
//                (transactionDto.shippingAddress().withStreet(null));
//
//        // When + Then
//        Flux<String> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);
//
//        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
//    }

//    @Test //todo: fix
//    void transactionBodyCheck_PartialFalseAndNullCity_ReturnsErrorMessage() {
//        // Given
//        TransactionDto transactionToCheck = transactionDto.withShippingAddress
//                (transactionDto.shippingAddress().withCity(null));
//
//        // When + Then
//        Flux<String> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);
//
//        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
//    }

//    @Test //todo: fix
//    void transactionBodyCheck_PartialFalseNullCountryAndNullCity_Returns2ErrorMessages() {
//        // Given
//        TransactionDto transactionToCheck = transactionDto.withShippingAddress
//                (transactionDto.shippingAddress().withCountry(null).withCity(null));
//
//        // When + Then
//        Flux<String> isValid = BodyCheckConfig.TRANSACTION_BODY_CHECK.apply(transactionToCheck);
//
//        StepVerifier.create(isValid).expectNextCount(2).verifyComplete();
//    }

}