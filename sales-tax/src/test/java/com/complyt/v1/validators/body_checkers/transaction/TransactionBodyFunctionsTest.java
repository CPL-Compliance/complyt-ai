package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.models.transaction.ItemDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TransactionBodyFunctionsTest {

    private ItemDto itemDto;
    private UnitTestUtilities testUtilities;


    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        itemDto = testUtilities.createItemDtos(true, false,true)
                .get(0);
    }

    @Test
    void getItemDtoTotal_ItemDtoTotalIsNotNull_ReturnsItemDtoTotal() {
        // given + When
        BigDecimal itemDtoTotal = TransactionBodyFunctions.getItemDtoTotal(itemDto);

        // Then
        assertEquals(itemDto.totalPrice(), itemDtoTotal);
    }

    @Test
    void getItemDtoTotal_ItemDtoTotalIsNull_ReturnsItemDtoQuantityMultiplyByUnitPrice() {
        // given
        ItemDto itemWithNullTotal = itemDto.withTotalPrice(null);
        BigDecimal expectedValue = itemWithNullTotal.quantity()
                .multiply(itemWithNullTotal.unitPrice());

        BigDecimal itemDtoTotal = TransactionBodyFunctions.getItemDtoTotal(itemWithNullTotal);

        // When + Then
        assertEquals(expectedValue, itemDtoTotal);
    }

    @Test
    void getItemDtoTotal_ItemDtoTotalIsNullAndUnitPriceIsNullQuantityIsNot0_ReturnsBigDecimal0() {
        // given
        ItemDto itemWithNullTotal = itemDto.withTotalPrice(null)
                .withUnitPrice(null);

        BigDecimal itemDtoTotal = TransactionBodyFunctions.getItemDtoTotal(itemWithNullTotal);

        // When + Then
        assertEquals(BigDecimal.ZERO, itemDtoTotal);
    }

    @Test
    void getItemDtoTotal_ItemDtoTotalIsNullAndQuantityIsNullUnitPriceIsNot0_ReturnsBigDecimal0() {
        // given
        ItemDto itemWithNullTotal = itemDto.withTotalPrice(null)
                .withQuantity(null);

        BigDecimal itemDtoTotal = TransactionBodyFunctions.getItemDtoTotal(itemWithNullTotal);

        // When + Then
        assertEquals(BigDecimal.ZERO, itemDtoTotal);
    }

    @Test
    void getItemDtoTotal_ItemDtoTotalIsNullQuantityIsNullUnitPriceIsNull_ReturnsBigDecimal0() {
        // given
        ItemDto itemWithNullTotal = itemDto.withTotalPrice(null)
                .withQuantity(null);

        BigDecimal itemDtoTotal = TransactionBodyFunctions.getItemDtoTotal(itemWithNullTotal);

        // When + Then
        assertEquals(BigDecimal.ZERO, itemDtoTotal);
    }

}