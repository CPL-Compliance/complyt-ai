package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.javatuples.Pair;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

@Component
@AllArgsConstructor
public class TransactionTotalAmountChecker implements DtoBodyChecker<TransactionDto>, TransactionBodyFunctions {

    @Override
    public Flux<String> check(@NonNull TransactionDto transactionDto) {
        return Flux.from(Mono.just(transactionDto.items())
                        .map(calculateTotalItemsAmountAfterDiscount())
                .flatMap(this::checkTransactionTotalAmountIsNotBelowZero));
    }

    // calculating the total amount and adding the discount.
    // discount is being received positively (hence, the usage of BigDecimal.subtract and not BigDecimal.add
    // this ignores if the discount is before tax or after tax
    private Function<List<ItemDto>, BigDecimal> calculateTotalItemsAmountAfterDiscount() {
        return (itemsDtoList) ->
                itemsDtoList.stream()
                        .map(TransactionBodyFunctions::getItemDtoTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .subtract(itemsDtoList.stream()
                                .map(ItemDto::discount)
                                .filter(Objects::nonNull)
                                .reduce(BigDecimal.ZERO, BigDecimal::add));
    }

    private Mono<String> checkTransactionTotalAmountIsNotBelowZero(BigDecimal transactionTotalAmount) {
        return transactionTotalAmount.compareTo(BigDecimal.ZERO) >= 0 ?
                Mono.empty() :
                Mono.just(DtoErrorMessages.TOTAL_AMOUNT_AFTER_DISCOUNT_IS_BELOW_ZERO);
    }
}
