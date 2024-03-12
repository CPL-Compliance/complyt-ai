package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public class NegativeItemsNotHavingDiscountChecker implements DtoBodyChecker<TransactionDto>, TransactionBodyFunctions {
    @Override
    public Flux<String> check(TransactionDto transactionDto) {
        return Flux.from(Mono.just(transactionDto.items())
                .flatMap(checkNegativeTotalItemDoesNotHaveDiscount()));
    }


    private Function<List<ItemDto>, Mono<String>> checkNegativeTotalItemDoesNotHaveDiscount() {
        return itemDtos -> itemDtos.stream()
                .filter(itemDto -> TransactionBodyFunctions.getItemDtoTotal(itemDto).compareTo(BigDecimal.ZERO) < 0)
                .anyMatch(itemDto -> itemDto.discount() != null) ?
                Mono.just(DtoErrorMessages.ITEM_WITH_NEGATIVE_TOTAL_CANNOT_HAVE_A_DISCOUNT) :
                Mono.empty();
    }


}
