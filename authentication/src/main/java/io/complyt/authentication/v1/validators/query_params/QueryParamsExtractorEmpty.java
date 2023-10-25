package io.complyt.authentication.v1.validators.query_params;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class QueryParamsExtractorEmpty<T> implements QueryParamsExtractor<T> {
    @Override
    public Mono<T> extract(ServerRequest serverRequest) {
        return Mono.empty();
    }
}