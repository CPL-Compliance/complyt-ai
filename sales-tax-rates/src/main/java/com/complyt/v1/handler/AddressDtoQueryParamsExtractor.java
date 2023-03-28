package com.complyt.v1.handler;

import com.complyt.v1.model.AddressDto;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Component
public class AddressDtoQueryParamsExtractor {

    public Mono<AddressDto> extract(ServerRequest serverRequest) {
        String state = serverRequest.queryParam("state").orElse("");
        String city = serverRequest.queryParam("city").orElse("");
        String street = serverRequest.queryParam("street").orElse("");
        String zip = serverRequest.queryParam("zip").orElse("");
        AddressDto address = new AddressDto(city, "US", null, state, street, zip);

        return Mono.just(address);
    }

}
