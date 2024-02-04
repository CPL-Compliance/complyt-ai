package com.complyt.v1.validators.body_checkers;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;

public class NegativeItemsNotHavingDiscountChecker implements DtoBodyChecker<TransactionDto> {
    @Override
    public Flux<String> check(TransactionDto transactionDto) {
        return Flux.from(Mono.just(transactionDto.items())
                .flatMap(checkNegativeTotalItemDoesNotHaveDiscount()));
    }


    private Function<List<ItemDto>, Mono<String>> checkNegativeTotalItemDoesNotHaveDiscount() {
        return itemDtos -> itemDtos.stream()
                .filter(itemDto -> itemDto.totalPrice().compareTo(BigDecimal.ZERO) < 0)
                .filter(itemDto -> itemDto.discount() != null)
                .count() != 0 ?
                Mono.just(DtoErrorMessages.ITEM_WITH_NEGATIVE_TOTAL_CANNOT_HAVE_A_DISCOUNT) :
                Mono.empty();
    }


}
