package com.complyt.v1.config;

import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.UUID;

public class BodyCheckConfigTest {

    UnitTestUtilities unitTestUtilities;
    TransactionDto transactionDto;

    BodyCheckConfig bodyCheckConfig;

    @BeforeEach
    void setUp() {
        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
        transactionDto = unitTestUtilities.createTransactionDto(UUID.toString())
                .withShippingAddress(UnitTestUtilities.createAddressDtoInCalifornia());
        bodyCheckConfig = unitTestUtilities.createBodyCheckConfig();
    }

    @Test
    void transactionBodyCheck_PartialAddress_ReturnsEmptyFlux() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withPartial(true));

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullStreet_ReturnsErrorMessage() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withStreet(null));

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseAndNullCity_ReturnsErrorMessage() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withCity(null));

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseNullCountryAndNullCity_Returns2ErrorMessages() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withCountry(null).withCity(null));

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(2).verifyComplete();
    }

    @Test
    void transactionBodyCheck_WithNegativeDiscountPositiveTotalAfterDiscount_ReturnsEmptyFlux() {
        // Given
        BigDecimal negativeItemsTotalAmount = transactionDto.items().stream()
                .map(ItemDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(-1));

        TransactionDto transactionToCheck = transactionDto
                .withDiscount(unitTestUtilities.createDiscountDto(negativeItemsTotalAmount.divide(BigDecimal.valueOf(2)), false, "")); //the total item amount is 160000

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
    }

    @Test
    void transactionBodyCheck_WithNegativeDiscountTotalAfterDiscountZero_ReturnsEmptyFlux() {
        // Given
        BigDecimal negativeItemsTotalAmount = transactionDto.items().stream()
                .map(ItemDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(-1));

        TransactionDto transactionToCheck = transactionDto
                .withDiscount(unitTestUtilities.createDiscountDto(negativeItemsTotalAmount, false, "")); //the total item amount is 160000

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
    }


    @Test
    void transactionBodyCheck_WithPositiveDiscountPositiveTotalAfterDiscount_ReturnsEmptyFlux() {
        // Given

        BigDecimal positiveItemsTotalAmount = transactionDto.items().stream()
                .map(ItemDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        TransactionDto transactionToCheck = transactionDto
                .withDiscount(unitTestUtilities.createDiscountDto(positiveItemsTotalAmount, false, "")); //the total item amount is 160000

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
    }


    @Test
    void transactionBodyCheck_WithNegativeTotalAfterDiscount_ReturnsDiscountErrorMessages() {

        // Given
        BigDecimal negativeItemsTotalAmount = transactionDto.items().stream()
                .map(ItemDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(-1));

        TransactionDto transactionToCheck = transactionDto
                .withDiscount(unitTestUtilities.createDiscountDto(negativeItemsTotalAmount.subtract(BigDecimal.ONE), false, "")); //the total item amount is 160000

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }


    @Test
    void transactionBodyCheck_WithDiscountOfZeroPositiveAmount_ReturnEmptyFlux() {
        // Given
        TransactionDto transactionToCheck = transactionDto
                .withDiscount(unitTestUtilities.createDiscountDto(BigDecimal.ZERO, false, "")); //the total item amount is 160000

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
    }

    @Test
    void transactionBodyCheck_WithDiscountNull_ReturnsEmptyFlux() {
        // Given
        TransactionDto transactionToCheck = transactionDto
                .withDiscount(null);

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(0).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialFalseNullStreetAndCountryAndNullCityAndDiscountWithNegativeTotalAfterDiscount_Returns4ErrorMessages() {
        // Given

        BigDecimal negativeItemsTotalAmount = transactionDto.items().stream()
                .map(ItemDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(-1));

        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                (transactionDto.shippingAddress().withStreet(null).withCountry(null).withCity(null))
                .withDiscount(unitTestUtilities.createDiscountDto(negativeItemsTotalAmount.subtract(BigDecimal.ONE), false, "")); //the total item amount is 160000

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(4).verifyComplete();
    }

    @Test
    void transactionBodyCheck_PartialTrueNullStreetAndCountryAndNullCityAndDiscountWithNegativeTotalAfterDiscount_Returns1ErrorMessages() {
        // Given

        BigDecimal negativeItemsTotalAmount = transactionDto.items().stream()
                .map(ItemDto::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .multiply(BigDecimal.valueOf(-1));

        TransactionDto transactionToCheck = transactionDto.withShippingAddress
                        (transactionDto.shippingAddress().withStreet(null).withCountry(null).withCity(null).withPartial(true))
                .withDiscount(unitTestUtilities.createDiscountDto(negativeItemsTotalAmount.subtract(BigDecimal.ONE), false, ""));

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

}