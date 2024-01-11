package com.complyt.v1.validators.body_checkers;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.DiscountDto;
import com.complyt.v1.models.transaction.ItemDto;
import com.complyt.v1.models.transaction.TransactionDto;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.BiFunction;

@Component
@AllArgsConstructor
public class TransactionTotalAmountChecker implements DtoBodyChecker<TransactionDto> {

    @Override
    public Flux<String> check(@NonNull TransactionDto transactionDto) {
        return Flux.from(checkIfDiscountExist(transactionDto)
                .flatMap(discountDto ->
                        calculateTotalItemsAmountAfterDiscount().apply(transactionDto.items(), discountDto))
                .flatMap(this::checkTransactionTotalAmountIsNotBelowZero));
    }

    private Mono<DiscountDto> checkIfDiscountExist(@NonNull TransactionDto transactionDto) {
        return transactionDto.discount() != null ?
                Mono.just(transactionDto.discount()) :
                Mono.empty();
    }


    // calculating the total amount and adding the discount.
    // discount is being received negatively (hence, the usage of BigDecimal.add and not BigDecimal.subtract
    // this ignores if the discount is before tax or after tax
    private BiFunction<List<ItemDto>, DiscountDto, Mono<BigDecimal>> calculateTotalItemsAmountAfterDiscount() {
        return (itemsDtoList, discountDto) ->
                Mono.just(itemsDtoList.stream()
                        .map(ItemDto::totalPrice)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .add(discountDto.discountAmount()));
    }

    private Mono<String> checkTransactionTotalAmountIsNotBelowZero(BigDecimal transactionTotalAmount) {
        return transactionTotalAmount.compareTo(BigDecimal.ZERO) >= 0 ?
                Mono.empty() :
                Mono.just(DtoErrorMessages.TOTAL_AMOUNT_AFTER_DISCOUNT_IS_BELOW_ZERO);
    }
}
