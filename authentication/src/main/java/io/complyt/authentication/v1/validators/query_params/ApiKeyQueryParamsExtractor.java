package io.complyt.authentication.v1.validators.query_params;

import io.complyt.authentication.utils.observability.ContextLogger;
import io.complyt.authentication.v1.models.ApiKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ApiKeyQueryParamsExtractor implements QueryParamsExtractor<ApiKey> {

    public Mono<ApiKey> extract(ServerRequest serverRequest) {
        String apiKeyStr = serverRequest.queryParam("api_key").orElse(null);

        ApiKey apiKey = new ApiKey(apiKeyStr);

        return ContextLogger.observeCtx("ApiKey extracted from request query params", log::info)
                .then(Mono.just(apiKey));
    }
}
