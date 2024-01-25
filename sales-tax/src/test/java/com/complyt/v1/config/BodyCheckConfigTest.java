package com.complyt.v1.config;

import com.complyt.domain.transaction.Item;
import com.complyt.v1.models.TangibleCategoryDto;
import com.complyt.v1.models.TaxableCategoryDto;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
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

        List<ItemDto> itemDtosList = List.of(
                new ItemDto(new BigDecimal(200), new BigDecimal(4), new BigDecimal(-800), "description", "name", "C1S1", null,
                        null, false, BigDecimal.ZERO, null, TaxableCategoryDto.TAXABLE),
        new ItemDto(new BigDecimal(-2000), new BigDecimal(4), new BigDecimal(-8000), "description", "name", "C3S1", null,
                null, false, BigDecimal.ZERO, null, TaxableCategoryDto.TAXABLE));

        TransactionDto transactionToCheck = transactionDto.withItems(itemDtosList);

        // When + Then
        Flux<String> isValid = bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);

        // error is transaction total amount is below 0 and item not aligned
        StepVerifier.create(isValid).expectNextCount(2).verifyComplete();
    }

    @Test
    void transactionBodyCheck_SendsNullTransactionDto_huh() {
        // Given
        TransactionDto transactionToCheck = null;

        // When + Then

        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            bodyCheckConfig.transactionDtoFluxFunction().apply(transactionToCheck);
        });

        assertEquals(nullPointerException.getMessage(), "transactionDto is marked non-null but is null");
    }
}