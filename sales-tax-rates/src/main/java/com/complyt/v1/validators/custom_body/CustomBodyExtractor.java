package com.complyt.v1.validators.custom_body;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public interface CustomBodyExtractor<T> {
    Mono<T> extract(ServerRequest serverRequest);
}