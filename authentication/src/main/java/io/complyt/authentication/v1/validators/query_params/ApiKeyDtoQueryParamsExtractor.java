package io.complyt.authentication.v1.validators.query_params;

import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.models.ApiKeyDto;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ApiKeyDtoQueryParamsExtractor implements QueryParamsExtractor<ApiKeyDto> {
    public Mono<ApiKeyDto> extract(@NonNull ServerRequest serverRequest) {
        String apiKeyStr = serverRequest.queryParam("api_key").orElse(null);
        ApiKeyDto apiKeyDto = new ApiKeyDto(apiKeyStr);

        return ContextLogger.observeCtx("ApiKey extracted from request query params", log::info)
                .then(Mono.just(apiKeyDto));
    }
}
