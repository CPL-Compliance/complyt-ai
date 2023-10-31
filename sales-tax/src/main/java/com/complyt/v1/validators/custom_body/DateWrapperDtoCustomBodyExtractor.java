package com.complyt.v1.validators.custom_body;

import com.complyt.v1.models.nexus.DateWrapperDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class DateWrapperDtoCustomBodyExtractor implements CustomBodyExtractor<DateWrapperDto> {
    @Override
    public Mono<DateWrapperDto> extract(ServerRequest serverRequest) {
        return Mono.just(new DateWrapperDto(serverRequest.queryParam("date").orElse("")));
    }
}