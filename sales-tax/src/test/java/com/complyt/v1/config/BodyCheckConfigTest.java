package com.complyt.v1.config;

import com.complyt.v1.models.TaxableCategoryDto;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.transaction.ItemsAlignmentChecker;
import com.complyt.v1.validators.body_checkers.transaction.TransactionDtoShippingAddressChecker;
import com.complyt.v1.validators.body_checkers.transaction.TransactionTotalAmountChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.UUID;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
    void transactionBodyCheck_WithNegativeTotalItemWithPositiveAmount_Returns1ErrorMessage() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withItems(List.of(
                unitTestUtilities.createItemDtoWithNegativeAmount(true, true)
                        .withTotalPrice(BigDecimal.valueOf(10000))
        ));

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        // error is items not aligned
        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

    @Test
    void transactionBodyCheck_WithNegativeTotalItemWithNegativeAmount_Returns1ErrorMessage() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withItems(List.of(
                unitTestUtilities.createItemDtoWithNegativeAmount(true, true)
                        .withTotalPrice(BigDecimal.valueOf(-10000))
        ));

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        // error is transaction total amount is below 0
        StepVerifier.create(isValid).expectNextCount(1).verifyComplete();
    }

    @Test
    void transactionBodyCheck_WithNegativeTotalAndItemNotAligned_Returns2ErrorMessages() {
        // Given
        TransactionDto transactionToCheck = transactionDto.withItems(List.of(
                unitTestUtilities.createItemDtoWithNegativeAmount(true, true)
                        .withTotalPrice(BigDecimal.valueOf(-10000))
                        .withUnitPrice(BigDecimal.valueOf(1000))
                        .withQuantity(BigDecimal.valueOf(1))
        ));


        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        // error is transaction total amount is below 0 and item not aligned
        StepVerifier.create(isValid).expectNextCount(2).verifyComplete();
    }

    @Test
    void TransactionDtoShippingAddressChecker_SendsNullTransactionDto_ReturnErrorMessage() {
        // Given
        TransactionDto transactionToCheck = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new TransactionDtoShippingAddressChecker().check(transactionToCheck);
        });

        assertEquals(nullPointerException.getMessage(), "transactionDto is marked non-null but is null");
    }

    @Test
    void TransactionTotalAmountChecker_SendsNullTransactionDto_ReturnErrorMessage() {
        // Given
        TransactionDto transactionToCheck = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new TransactionTotalAmountChecker().check(transactionToCheck);
        });

        assertEquals(nullPointerException.getMessage(), "transactionDto is marked non-null but is null");
    }

    @Test
    void ItemsAlignmentChecker_SendsNullTransactionDto_ReturnErrorMessage() {
        // Given
        TransactionDto transactionToCheck = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            new ItemsAlignmentChecker().check(transactionToCheck);
        });

        assertEquals(nullPointerException.getMessage(), "transactionDto is marked non-null but is null");
    }
}