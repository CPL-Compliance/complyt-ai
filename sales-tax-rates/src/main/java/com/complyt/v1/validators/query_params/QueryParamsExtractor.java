package com.complyt.v1.validators.query_params;

import com.complyt.v1.validators.custom_body.CustomBodyExtractor;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

public interface QueryParamsExtractor<T> extends CustomBodyExtractor<T> {
    Mono<T> extract(ServerRequest serverRequest);
}
