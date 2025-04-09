package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.security.TenantResolver;
import com.complyt.v1.models.transaction.ItemDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import testUtils.unit_test.UnitTestUtilities;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mockStatic;

class TransactionBodyFunctionsTest {

    private ItemDto itemDto;
    private UnitTestUtilities testUtilities;


     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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