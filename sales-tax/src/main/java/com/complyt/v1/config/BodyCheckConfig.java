package com.complyt.v1.config;

import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.config.error_messages.StringErrorMessages;
import com.complyt.v1.models.transaction.TransactionDto;
import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BodyCheckConfig {

    @NonNull
    List<DtoBodyChecker> dtoBodyCheckersList;

    public Function<TransactionDto, Flux<String>> transactionDtoFluxFunction() {
        return transactionDto ->
                dtoBodyCheckersList.stream()
                                .map(dtoBodyChecker -> dtoBodyChecker.check(transactionDto))
                        .reduce(Flux.empty(), (resultFlux, element) -> resultFlux.concatWith(element));

    }
}