package com.complyt.v1.validators.query_params;

import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.model.AddressDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AddressDtoQueryParamsExtractor implements QueryParamsExtractor<AddressDto> {

    public Mono<AddressDto> extract(ServerRequest serverRequest) {
        String state = serverRequest.queryParam("state").orElse("");
        String city = serverRequest.queryParam("city").orElse("");
        String street = serverRequest.queryParam("street").orElse("");
        String zip = serverRequest.queryParam("zip").orElse("");
        AddressDto address = new AddressDto(city, "US", null, state, street, zip);

        return ContextLogger.observeCtx("Address extracted from request query params: " + address, log::debug)
                .then(Mono.just(address));
    }

}
