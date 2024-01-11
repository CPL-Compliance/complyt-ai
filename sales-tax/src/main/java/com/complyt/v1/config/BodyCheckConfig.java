package com.complyt.v1.config;

import com.complyt.business.builder.CollectionBuilder;
import com.complyt.business.transaction.items_amounts.TotalItemsAmountCalculator;
import com.complyt.domain.Taxable;
import com.complyt.domain.transaction.Transaction;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.transaction.DiscountDto;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import com.complyt.v1.validators.body_checkers.TransactionDtoShippingAddressChecker;
import com.complyt.v1.validators.body_checkers.TransactionTotalAmountChecker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.pl.NIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BodyCheckConfig {

    @NonNull
    List<DtoBodyChecker> dtoBodyCheckersList;

    public Function<TransactionDto, Flux<String>> transactionDtoFluxFunction() {
        return transactionDto ->
                dtoBodyCheckersList.stream()
                                .map(dtoBodyChecker -> dtoBodyChecker.check(transactionDto))
                        .reduce(Flux.empty(), (result, element) -> result.concatWith(element));

    }
}