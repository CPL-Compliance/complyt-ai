package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import lombok.NonNull;
import reactor.core.publisher.Flux;

public class ItemsAlignmentChecker implements DtoBodyChecker<TransactionDto> {

    // this test checks if the sign of the transaction total price is the same
    // as the multiplying between the amount and the unit price
    // if the unit price is positive, and the amount is positive
    // (we have @positive jakarta annotation in the dto)
    // the multiplying of them should be positive
    // and vice versa if the unit price is negative
    // this check was added when supporting negative total items was started
    @Override
    public Flux<String> check(@NonNull TransactionDto transactionDto) {
        return transactionDto.items().stream()
                .map(this::checkItemAlignment)
                .reduce(true, Boolean::logicalAnd) ?
                Flux.empty() :
                Flux.just(DtoErrorMessages.ONE_OF_THE_ITEMS_IS_UNALIGNED);
    }

    private boolean checkItemAlignment(ItemDto itemDto) {
        return itemDto.totalPrice() == null || itemDto.unitPrice() == null || itemDto.quantity() == null ||
                // ItemHaveEitherTotalOrUnitPriceAndQuantity checks if at least one kind of "total" was received
                itemDto.totalPrice().signum() == itemDto.unitPrice().multiply(itemDto.quantity()).signum();
    }

}
