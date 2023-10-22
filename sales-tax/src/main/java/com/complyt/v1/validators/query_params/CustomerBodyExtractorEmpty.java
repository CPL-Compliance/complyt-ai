package com.complyt.v1.validators.query_params;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class CustomerBodyExtractorEmpty<T> implements CustomerBodyExtractor<T> {
    @Override
    public Mono<T> extract(ServerRequest serverRequest) {
        return Mono.empty();
    }
}