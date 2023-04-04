package com.complyt.v1.validators.query_params;

import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface QueryParamsExtractor<T> {
    Mono<T> extract(ServerRequest serverRequest);
}
