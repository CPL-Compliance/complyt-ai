package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.models.transaction.ItemDto;

import java.math.BigDecimal;

public interface TransactionBodyFunctions {
    static BigDecimal getItemDtoTotal(ItemDto itemDto) {
        return itemDto.totalPrice() != null ?
                itemDto.totalPrice() :
                itemDto.quantity() != null && itemDto.unitPrice() != null ?
                        itemDto.quantity().multiply(itemDto.unitPrice()) :

                        // if we reach here, ItemHaveEitherTotalOrUnitPriceAndQuantity should fail the transaction anyway
                        BigDecimal.ZERO;
    }
}
