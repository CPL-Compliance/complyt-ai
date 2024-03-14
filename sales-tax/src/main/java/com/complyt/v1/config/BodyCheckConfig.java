package com.complyt.v1.config;

import com.complyt.v1.validators.body_checkers.DtoBodyChecker;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.function.Function;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BodyCheckConfig<T> {

    @NonNull
    List<DtoBodyChecker> dtoBodyCheckersList;

    public Function<T, Flux<String>> entityDtoFluxFunction() {
        return entityDto ->
                dtoBodyCheckersList.stream()
                                .map(dtoBodyChecker -> dtoBodyChecker.check(entityDto))
                        .reduce(Flux.empty(), (resultFlux, element) -> resultFlux.concatWith(element));

    }
}