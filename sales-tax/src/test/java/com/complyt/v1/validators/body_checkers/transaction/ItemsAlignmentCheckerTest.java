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

class ItemsAlignmentCheckerTest {
    private ItemDto itemDto;
    private TransactionDto transactionDto;
    private UnitTestUtilities testUtilities;
    private ItemsAlignmentChecker itemsAlignmentChecker;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        itemDto = testUtilities.createTransactionDto("").items().get(0);
        transactionDto = testUtilities.createTransactionDto("")
                .withItems(List.of(itemDto));
        itemsAlignmentChecker = new ItemsAlignmentChecker();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceAndQuantityNotNullAndSignIsEqual_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto.withTotalPrice(BigDecimal.ZERO)
                        .withUnitPrice(BigDecimal.ZERO)
                        .withQuantity(BigDecimal.ZERO)
                        .withTotalPrice(BigDecimal.ONE)
                        .withQuantity(BigDecimal.ONE)
                        .withUnitPrice(BigDecimal.ONE)));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceAndQuantityNotNullAndSignIsNotEqualUnitPriceIsMinus_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto
                        .withTotalPrice(BigDecimal.ONE)
                        .withQuantity(BigDecimal.ONE)
                        .withUnitPrice(BigDecimal.valueOf(-1))));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ONE_OF_THE_ITEMS_IS_UNALIGNED).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceAndQuantityNotNullAndSignIsNotEqualQuantityIsMinus_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto
                        .withTotalPrice(BigDecimal.ONE)
                        .withQuantity(BigDecimal.valueOf(-1))
                        .withUnitPrice(BigDecimal.ONE)));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ONE_OF_THE_ITEMS_IS_UNALIGNED).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceAndQuantityNotNullAndSignIsNotEqualTotalIsMinus_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto
                        .withTotalPrice(BigDecimal.valueOf(-1))
                        .withQuantity(BigDecimal.ONE)
                        .withUnitPrice(BigDecimal.ONE)));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ONE_OF_THE_ITEMS_IS_UNALIGNED).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceAndQuantityNotNullAndSignIEqualAllAreZero_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto
                        .withTotalPrice(BigDecimal.ZERO)
                        .withQuantity(BigDecimal.ZERO)
                        .withUnitPrice(BigDecimal.ZERO)));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithQuantityAndUnitPriceNotNullAndTotalNullAndSignIsEqual_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto
                        .withTotalPrice(null)
                        .withQuantity(BigDecimal.ONE)
                        .withUnitPrice(BigDecimal.ONE)));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndUnitPriceNotNullAndQuantityNullAndSignIsEqual_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto
                        .withTotalPrice(BigDecimal.ONE)
                        .withQuantity(null)
                        .withUnitPrice(BigDecimal.ONE)));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemDtoWithTotalAndQuantityNotNullAndUnitPriceNullAndSignIsEqual_ReturnEmpty() {
        // Given
        TransactionDto transactionWithFullItemDto = transactionDto
                .withItems(List.of(itemDto
                        .withTotalPrice(BigDecimal.ONE)
                        .withQuantity(BigDecimal.ONE)
                        .withUnitPrice(null)));
        // When
        Flux<String> errorStringFlux = itemsAlignmentChecker
                .check(transactionWithFullItemDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }
}
