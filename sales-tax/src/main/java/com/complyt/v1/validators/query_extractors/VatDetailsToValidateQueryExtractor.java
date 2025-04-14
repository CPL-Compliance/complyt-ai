package com.complyt.v1.validators.query_extractors;

import com.complyt.utils.observability.ContextLogger;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import com.complyt.v1.validators.custom_body.CustomBodyExtractor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
public class VatDetailsToValidateQueryExtractor implements CustomBodyExtractor<VatDetailsToValidateDto> {
    public Mono<VatDetailsToValidateDto> extract(ServerRequest serverRequest) {
        String countryCode = serverRequest.queryParam("countryCode").orElse(null);
        String vatNumber = serverRequest.queryParam("vatNumber").orElse(null);

        VatDetailsToValidateDto vatDetailsToValidateDto = new VatDetailsToValidateDto(countryCode, vatNumber);
        return ContextLogger.observeCtx("Vat Details extracted from request query params: " + vatDetailsToValidateDto, log::info)
                .then(Mono.just(vatDetailsToValidateDto));
    }
}
