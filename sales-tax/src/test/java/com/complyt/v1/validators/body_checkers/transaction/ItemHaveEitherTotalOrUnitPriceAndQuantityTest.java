package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
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
import java.util.UUID;

class ItemHaveEitherTotalOrUnitPriceAndQuantityCheckerTest {
    private ItemDto itemDto;
    private TransactionDto transactionDto;
    private UnitTestUtilities testUtilities;
    private ItemHaveEitherTotalOrUnitPriceAndQuantityChecker itemHaveEitherTotalOrUnitPriceAndQuantityChecker;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transactionDto = testUtilities.createTransactionDto("");
        itemDto = transactionDto.items().get(0);
        itemHaveEitherTotalOrUnitPriceAndQuantityChecker = new ItemHaveEitherTotalOrUnitPriceAndQuantityChecker();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceAndQuantityNotNull_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto.withTotalPrice(BigDecimal.ZERO)
                        .withUnitPrice(BigDecimal.ZERO)
                        .withQuantity(BigDecimal.ZERO)));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceAndQuantityNull_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto.withTotalPrice(null)
                        .withUnitPrice(null)
                        .withQuantity(null)));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNext(DtoErrorMessages.ITEMS_MISSING_TOTAL_OR_QUANTITY_AND_UNITPRICE).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalNullAndUnitPriceAndQuantityNotNull_ReturnEmpty() {
        // Given
        TransactionDto transactionNullTotalItemDto = transactionDto
                .withItems(List.of(itemDto.withTotalPrice(null)
                        .withUnitPrice(BigDecimal.ZERO)
                        .withQuantity(BigDecimal.ZERO)));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionNullTotalItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalNotNullAndUnitPriceAndQuantityNull_ReturnEmpty() {
        // Given
        TransactionDto transactionNullTotalItemDto = transactionDto
                .withItems(List.of(itemDto.withTotalPrice(BigDecimal.ZERO)
                        .withUnitPrice(null)
                        .withQuantity(null)));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionNullTotalItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceNullAndQuantityNotNull_ReturnFluxError() {
        // Given
        TransactionDto transactionProblematicItemDto = transactionDto
                .withItems(List.of(itemDto.withTotalPrice(null)
                        .withUnitPrice(null)
                        .withQuantity(BigDecimal.ZERO)));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionProblematicItemDto);

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ITEMS_MISSING_TOTAL_OR_QUANTITY_AND_UNITPRICE).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndQuantityNullAndUnitPriceNotNull_ReturnFluxError() {
        // Given
        TransactionDto transactionProblematicItemDto = transactionDto
                .withItems(List.of(itemDto.withTotalPrice(null)
                        .withUnitPrice(BigDecimal.ZERO)
                        .withQuantity(null)));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionProblematicItemDto);

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ITEMS_MISSING_TOTAL_OR_QUANTITY_AND_UNITPRICE).verifyComplete();
    }

    @Test
    public void check_2ItemsDtoWithTotalAndQuantityNullAndUnitPriceNotNull_ReturnFluxError() {
        // Given
        ItemDto problematicItemDto = itemDto.withTotalPrice(null)
                .withUnitPrice(BigDecimal.ZERO)
                .withQuantity(null);

        TransactionDto transactionNullTotalItemDto = transactionDto
                .withItems(List.of(
                        problematicItemDto.withName(""),
                        problematicItemDto.withName("")));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionNullTotalItemDto);

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ITEMS_MISSING_TOTAL_OR_QUANTITY_AND_UNITPRICE)
                .verifyComplete();
    }

    @Test
    public void check_2ItemsDto1WithTotalAndQuantityNullAndUnitPriceNotNullAnd1Ok_ReturnFluxError() {
        // Given
        ItemDto problematicItemDto = itemDto.withTotalPrice(null)
                .withUnitPrice(BigDecimal.ZERO)
                .withQuantity(null);

        TransactionDto transactionProblematicItemDto = transactionDto
                .withItems(List.of(
                        transactionDto.items().get(0),
                        problematicItemDto.withName("")));
        // When
        Flux<String> errorStringFlux = itemHaveEitherTotalOrUnitPriceAndQuantityChecker
                .check(transactionProblematicItemDto);

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ITEMS_MISSING_TOTAL_OR_QUANTITY_AND_UNITPRICE).verifyComplete();
    }

}