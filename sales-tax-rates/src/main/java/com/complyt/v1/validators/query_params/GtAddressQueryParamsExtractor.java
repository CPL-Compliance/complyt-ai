package com.complyt.v1.validators.query_params;

import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.model.gt.GtAddressDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class GtAddressQueryParamsExtractor implements QueryParamsExtractor<GtAddressDto> {
    @Override
    public Mono<GtAddressDto> extract(ServerRequest serverRequest) {
        String country = serverRequest.queryParam("country").orElse(null);
        String region = serverRequest.queryParam("region").orElse(null);

        GtAddressDto gstAddress = new GtAddressDto(country, region);

        return ContextLogger.observeCtx("GstAddress extracted from request query params: " + gstAddress, log::info)
                .then(Mono.just(gstAddress));
    }

}