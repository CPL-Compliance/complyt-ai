package com.complyt.v1.validators.query_params;

import com.complyt.v1.models.nexus.DateWrapperDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class DateWrapperDtoCustomerBodyExtractor implements CustomerBodyExtractor<DateWrapperDto> {
    @Override
    public Mono<DateWrapperDto> extract(ServerRequest serverRequest) {
        return Mono.just(new DateWrapperDto(serverRequest.queryParam("date").orElse("")));
    }
}