package io.complyt.files.v1.validators.query_params;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public interface QueryParamsExtractor<T> {
    Mono<T> extract(ServerRequest serverRequest);
}