package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import reactor.core.publisher.Flux;

public class ItemHaveEitherTotalOrUnitPriceAndQuantityChecker implements DtoBodyChecker<TransactionDto> {
    @Override
    public Flux<String> check(TransactionDto transactionDto) {
        return transactionDto.items().stream()
                .map(this::checkItemFieldsExists)
                .reduce(true, Boolean::logicalAnd) ?
                Flux.empty() :
                Flux.just(DtoErrorMessages.ITEMS_MISSING_TOTAL_OR_QUANTITY_AND_UNITPRICE);
    }

    private boolean checkItemFieldsExists(ItemDto itemDto) {
        return itemDto.totalPrice() != null || (itemDto.unitPrice() != null && itemDto.quantity() != null);
    }
}
