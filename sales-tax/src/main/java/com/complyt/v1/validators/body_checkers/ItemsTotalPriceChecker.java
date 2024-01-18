package com.complyt.v1.validators.body_checkers;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ItemsTotalPriceChecker implements DtoBodyChecker<TransactionDto> {
    @Override
    public Flux<String> check(TransactionDto transactionDto) {

        return transactionDto.items().stream()
                .map(this::checkItemAlignment)
                .reduce(true, Boolean::logicalAnd) ?
                Flux.empty() :
                Flux.just(DtoErrorMessages.ONE_OF_THE_ITEMS_IS_UNALIGNED);
    }

    private boolean checkItemAlignment(ItemDto itemDto) {
        return itemDto.totalPrice()
                .compareTo(itemDto.unitPrice().multiply(itemDto.quantity())) == 0;
    }
}
