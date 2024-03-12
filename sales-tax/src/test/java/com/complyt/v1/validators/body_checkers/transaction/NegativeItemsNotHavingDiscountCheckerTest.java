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

class NegativeItemsNotHavingDiscountCheckerTest {
    private ItemDto itemDto;
    private TransactionDto transactionDto;
    private UnitTestUtilities testUtilities;
    private NegativeItemsNotHavingDiscountChecker negativeItemsNotHavingDiscountChecker;


    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        itemDto = testUtilities.createItemDtoWithNegativeAmount(true, true);
        transactionDto = testUtilities.createTransactionDto("")
                .withItems(List.of(itemDto));
        negativeItemsNotHavingDiscountChecker = new NegativeItemsNotHavingDiscountChecker();
    }

    @Test
    public void check_ItemWithNegativeTotalAndNoDiscount_ReturnEmpty() {
        // Given + When
        Flux<String> errorStringFlux = negativeItemsNotHavingDiscountChecker
                .check(transactionDto);

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemWithPositiveTotalItemAndNegativeTotalItemNoDiscount_ReturnEmpty() {
        // Given + When
        ItemDto positiveTotalItemDto = itemDto.withTotalPrice(BigDecimal.valueOf(800));
        List<ItemDto> itemDtoList = List.of(
                transactionDto.items().get(0),
                positiveTotalItemDto
        );
        Flux<String> errorStringFlux = negativeItemsNotHavingDiscountChecker
                .check(transactionDto.withItems(itemDtoList));

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0).verifyComplete();
    }

    @Test
    public void check_ItemWithNegativeTotalItemDiscount_ReturnError() {
        // Given + When
        ItemDto itemWithNegativeTotalAndDiscount = itemDto.withDiscount(BigDecimal.valueOf(500));
        Flux<String> errorStringFlux = negativeItemsNotHavingDiscountChecker.
                check(transactionDto.withItems(List.of(itemWithNegativeTotalAndDiscount)));

        // Then
        StepVerifier.create(errorStringFlux)
                .expectNext(DtoErrorMessages.ITEM_WITH_NEGATIVE_TOTAL_CANNOT_HAVE_A_DISCOUNT)
                .verifyComplete();
    }

    @Test
    public void check_ItemWithNullTotalButNegativeUnitPriceItemAndDiscount_ReturnError() {
        // Given + When
        ItemDto itemWithNegativeUnitPriceAndDiscount = itemDto.withDiscount(BigDecimal.valueOf(500))
                .withTotalPrice(null)
                .withUnitPrice(BigDecimal.valueOf(-500));
        Flux<String> errorStringFlux = negativeItemsNotHavingDiscountChecker.
                check(transactionDto.withItems(List.of(itemWithNegativeUnitPriceAndDiscount)));

        // Then
        StepVerifier.create(errorStringFlux).expectNext(DtoErrorMessages.ITEM_WITH_NEGATIVE_TOTAL_CANNOT_HAVE_A_DISCOUNT)
                .verifyComplete();
    }

    @Test
    public void check_ItemWithPositiveTotalItemDiscountLargetThanTotal_ReturnEmpty() {
        // Given + When
        ItemDto itemWithPositiveTotalAndDiscountLargetThanTotal = itemDto.withDiscount(BigDecimal.valueOf(500))
                .withTotalPrice(BigDecimal.valueOf(499));
        Flux<String> errorStringFlux = negativeItemsNotHavingDiscountChecker.
                check(transactionDto.withItems(List.of(itemWithPositiveTotalAndDiscountLargetThanTotal)));

        // Then
        StepVerifier.create(errorStringFlux).expectNextCount(0)
                .verifyComplete();
    }

}