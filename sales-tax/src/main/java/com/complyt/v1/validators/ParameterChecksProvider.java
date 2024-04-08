package com.complyt.v1.validators;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.Function;

@AllArgsConstructor
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ParameterChecksProvider {
    Map<String, Function<String, Mono<String>>> paramChecksMap;

    public Mono<Function<String, Mono<String>>> getFunctionCheck(@NonNull String uriVariable) {
        Function<String, Mono<String>> check = paramChecksMap.get(uriVariable);
        return Mono.just(check == null ? (param) -> Mono.empty() : check);
    }

    public Mono<Boolean> doesParamExist(ServerRequest serverRequest) {
        return Mono.justOrEmpty(paramChecksMap.keySet().stream()
                .anyMatch(paramName -> serverRequest.queryParam(paramName).isPresent()));
    }

}
