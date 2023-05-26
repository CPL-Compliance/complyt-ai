package com.complyt.v1.validators.query_params;

import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.model.AddressDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
public class AddressDtoQueryParamsExtractor implements QueryParamsExtractor<AddressDto> {

    public Mono<AddressDto> extract(ServerRequest serverRequest) {
        String state = serverRequest.queryParam("state").orElse(null);
        String country = serverRequest.queryParam("country").orElse(null);
        String city = serverRequest.queryParam("city").orElse(null);
        String street = serverRequest.queryParam("street").orElse(null);
        String zip = serverRequest.queryParam("zip").orElse(null);
        String county = serverRequest.queryParam("county").orElse(null);
        AddressDto address = new AddressDto(city, country, county, state, street, zip);

        return ContextLogger.observeCtx("Address extracted from request query params: " + address, log::info)
                .then(Mono.just(address));
    }

}
