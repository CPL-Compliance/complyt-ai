package com.complyt.v1.validators.query_params;

import com.complyt.utils.ContextLogger;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.AddressWithDateDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Slf4j
@Component
public class AddressDtoQueryParamsExtractor implements QueryParamsExtractor<AddressWithDateDto> {

    public Mono<AddressWithDateDto> extract(ServerRequest serverRequest) {
        String state = serverRequest.queryParam("state").orElse(null);
        String country = serverRequest.queryParam("country").orElse(null);
        String city = serverRequest.queryParam("city").orElse(null);
        String street = serverRequest.queryParam("street").orElse(null);
        String zip = serverRequest.queryParam("zip").orElse(null);
        String county = serverRequest.queryParam("county").orElse(null);
        String effectiveDate = serverRequest.queryParam("effectiveDate").orElse(LocalDateTime.now().toString());
        boolean isPartial = serverRequest.queryParam("isPartial")
                .map(Boolean::valueOf)
                .orElse(false);

        AddressWithDateDto addressAndTransactionDateDto = new AddressWithDateDto(new AddressDto(city, country, county, state, street, zip, isPartial),
                effectiveDate);

        return ContextLogger.observeCtx("Address extracted from request query params: " + addressAndTransactionDateDto, log::info)
                .then(Mono.just(addressAndTransactionDateDto));
    }
}
